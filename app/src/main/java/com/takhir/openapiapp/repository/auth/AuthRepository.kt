package com.takhir.openapiapp.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.takhir.openapiapp.api.auth.OpenApiAuthService
import com.takhir.openapiapp.api.auth.network_responses.LoginResponse
import com.takhir.openapiapp.api.auth.network_responses.RegistrationResponse
import com.takhir.openapiapp.models.AccountProperties
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
import com.takhir.openapiapp.util.*
import com.takhir.openapiapp.util.ErrorHandling.Companion.ERROR_SAVE_ACCOUNT_PROPERTIES
import com.takhir.openapiapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.takhir.openapiapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.takhir.openapiapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.takhir.openapiapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
  val authTokenDao: AuthTokenDao,
  val accountPropertiesDao: AccountPropertiesDao,
  val openApiAuthService: OpenApiAuthService,
  val sessionManager: SessionManager,
  val sharedPreferences: SharedPreferences,
  val sharedPrefEditor: SharedPreferences.Editor
)
{

  private val TAG = "AppDebug"

  private var repositoryJob: Job? = null

  fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
    val loginFieldErrors = LoginFields(email, password).isValidForLogin()
    if (loginFieldErrors != LoginFields.LoginError.none()) {
      return returnErrorResponse(loginFieldErrors, ResponseType.Dialog)
    }

    return object: NetworkBoundResource<LoginResponse, Any,  AuthViewState>(
      sessionManager.isConnectedToTheInternet(),
      true,
      false
    ) {

      override suspend fun createCacheRequestAndReturn() {
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
        Log.d(TAG, "handleApiSuccessResponse: $response")

        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        if (response.body.equals(GENERIC_AUTH_ERROR)) {
          return onErrorReturn(response.body.errorMessage, true, false)
        }

        accountPropertiesDao.insertOrIgnore(
          AccountProperties(
            response.body.pk,
            response.body.email,
            ""
          )
        )

        val result = authTokenDao.insert(
          AuthToken(
            response.body.pk,
            response.body.token
          )
        )
        if (result < 0) {
          return onCompleteJob(
            DataState.error(
              Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
            )
          )
        }

        saveAuthenticatedUserToPrefs(email)

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

      override fun loadFromCache(): LiveData<AuthViewState> {
        return AbsentLiveData.create()
      }

      override suspend fun updateLocalDb(cacheObject: Any?) {}

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

    return object: NetworkBoundResource<RegistrationResponse, Any,  AuthViewState>(
      sessionManager.isConnectedToTheInternet(),
      true,
      false
    ) {

      override suspend fun createCacheRequestAndReturn() {
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {

        Log.d(TAG, "handleApiSuccessResponse: ${response}")

        if(response.body.response.equals(GENERIC_AUTH_ERROR)){
          return onErrorReturn(response.body.errorMessage, true, false)
        }

        val result1 = accountPropertiesDao.insertAndReplace(
          AccountProperties(
            response.body.pk,
            response.body.email,
            response.body.username
          )
        )

        // will return -1 if failure
        if(result1 < 0){
          onCompleteJob(DataState.error(
            Response(ERROR_SAVE_ACCOUNT_PROPERTIES, ResponseType.Dialog))
          )
          return
        }

        // will return -1 if failure
        val result2 = authTokenDao.insert(
          AuthToken(
            response.body.pk,
            response.body.token
          )
        )
        if(result2 < 0){
          onCompleteJob(DataState.error(
            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
          ))
          return
        }

        saveAuthenticatedUserToPrefs(email)

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

      override fun loadFromCache(): LiveData<AuthViewState> {
        return AbsentLiveData.create()
      }

      override suspend fun updateLocalDb(cacheObject: Any?) {}

    }.asLiveData()
  }

  fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
    val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

    if (previousAuthUserEmail.isNullOrBlank()) {
      Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found.")
      return returnNoTokenFound()
    } else {
      return object: NetworkBoundResource<Void, Any, AuthViewState>(
        sessionManager.isConnectedToTheInternet(),
        false,
        false
      ){

        override suspend fun createCacheRequestAndReturn() {
          accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
            Log.d(TAG, "createCacheRequestAndReturn: searching for token... account properties: $accountProperties")

            accountProperties?.let {
              if (accountProperties.pk > -1) {
                authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                  if (authToken?.token != null) {
                    onCompleteJob(
                      DataState.data(
                        AuthViewState(authToken = authToken)
                      )
                    )
                    return
                  }
                }
              }
            }

            Log.d(TAG, "createCacheRequestAndReturn: AuthToken not found...")
            onCompleteJob(
              DataState.data(
                null,
                Response(
                  RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                  ResponseType.None
                )
              )
            )
          }
        }

        override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
        }

        override fun createCall(): LiveData<GenericApiResponse<Void>> {
          return AbsentLiveData.create()
        }

        override fun setJob(job: Job) {
          repositoryJob?.cancel()
          repositoryJob = job
        }

        override fun loadFromCache(): LiveData<AuthViewState> {
          return AbsentLiveData.create()
        }

        override suspend fun updateLocalDb(cacheObject: Any?) {}

      }.asLiveData()
    }
  }

  private fun saveAuthenticatedUserToPrefs(email: String) {
    sharedPrefEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
    sharedPrefEditor.apply()
  }

  private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>>{
    return object: LiveData<DataState<AuthViewState>>(){
      override fun onActive() {
        super.onActive()
        value = DataState.data(null, Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None))
      }
    }
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