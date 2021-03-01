package com.takhir.openapiapp.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.takhir.openapiapp.api.auth.OpenApiAuthService
import com.takhir.openapiapp.api.auth.network_responses.LoginResponse
import com.takhir.openapiapp.api.auth.network_responses.RegistrationResponse
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.persistence.AuthTokenDao
import com.takhir.openapiapp.repository.NetworkBoundResource
import com.takhir.openapiapp.session.SessionManager
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.Response
import com.takhir.openapiapp.ui.ResponseType
import com.takhir.openapiapp.ui.auth.state.AuthViewState
import com.takhir.openapiapp.ui.auth.state.LoginFields
import com.takhir.openapiapp.ui.auth.state.RegistrationFields
import com.takhir.openapiapp.util.ApiEmptyResponse
import com.takhir.openapiapp.util.ApiErrorResponse
import com.takhir.openapiapp.util.ApiSuccessResponse
import com.takhir.openapiapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.takhir.openapiapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.takhir.openapiapp.util.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
  val authTokenDao: AuthTokenDao,
  val accountPropertiesDao: AccountPropertiesDao,
  val openApiAuthService: OpenApiAuthService,
  val sessionManager: SessionManager
)
{

  private val TAG = "AppDebug"

  private var repositoryJob: Job? = null

  fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
    val loginFieldErrors = LoginFields(email, password).isValidForLogin()
    if (loginFieldErrors != LoginFields.LoginError.none()) {
      return returnErrorResponse(loginFieldErrors, ResponseType.Dialog)
    }

    return object: NetworkBoundResource<LoginResponse, AuthViewState>(
      sessionManager.isConnectedToTheInternet()
    ) {

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
        Log.d(TAG, "handleApiSuccessResponse: $response")

        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        if (response.body.equals(GENERIC_AUTH_ERROR)) {
          return onErrorReturn(response.body.errorMessage, true, false)
        }

        onCompleteJob(
          DataState.data(
            data = AuthViewState(
              authToken = AuthToken(response.body.pk, response.body.token)
            )
          )
        )
      }

      override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
        return openApiAuthService.login(email, password)
      }

      override fun setJob(job: Job) {
        repositoryJob?.cancel()
        repositoryJob = job
      }

    }.asLiveData()
  }

  fun attemptRegistration(
    email: String,
    username: String,
    password: String,
    confirmPassword: String
  ): LiveData<DataState<AuthViewState>> {
    val registrationFields = RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
    if (registrationFields != RegistrationFields.RegistrationError.none()) {
      return returnErrorResponse(registrationFields, ResponseType.Dialog)
    }

    return object: NetworkBoundResource<RegistrationResponse, AuthViewState>(
      sessionManager.isConnectedToTheInternet()
    ) {
      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {

        Log.d(TAG, "handleApiSuccessResponse: ${response}")

        if(response.body.response.equals(GENERIC_AUTH_ERROR)){
          return onErrorReturn(response.body.errorMessage, true, false)
        }

        onCompleteJob(
          DataState.data(
            data = AuthViewState(
              authToken = AuthToken(response.body.pk, response.body.token)
            )
          )
        )
      }

      override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
        return openApiAuthService.register(email, username, password, confirmPassword)
      }

      override fun setJob(job: Job) {
        repositoryJob?.cancel()
        repositoryJob = job
      }

    }.asLiveData()
  }

  private fun returnErrorResponse(
    errorMessage: String,
    responseType: ResponseType
  ): LiveData<DataState<AuthViewState>> {
    return object: LiveData<DataState<AuthViewState>>() {
      override fun onActive() {
        super.onActive()
        value = DataState.error(
          Response(
            errorMessage,
            responseType
          )
        )
      }
    }
  }

  fun cancelActiveJobs(){
    Log.d(TAG, "AuthRepository: Cancelling on-going jobs...")
    repositoryJob?.cancel()
  }
}