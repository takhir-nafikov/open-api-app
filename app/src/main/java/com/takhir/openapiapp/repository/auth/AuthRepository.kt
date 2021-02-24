package com.takhir.openapiapp.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.takhir.openapiapp.api.auth.OpenApiAuthService
import com.takhir.openapiapp.api.auth.network_responses.LoginResponse
import com.takhir.openapiapp.api.auth.network_responses.RegistrationResponse
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.persistence.AuthTokenDao
import com.takhir.openapiapp.session.SessionManager
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.Response
import com.takhir.openapiapp.ui.ResponseType
import com.takhir.openapiapp.ui.auth.state.AuthViewState
import com.takhir.openapiapp.util.ApiEmptyResponse
import com.takhir.openapiapp.util.ApiErrorResponse
import com.takhir.openapiapp.util.ApiSuccessResponse
import com.takhir.openapiapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.takhir.openapiapp.util.GenericApiResponse
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

  fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
    return openApiAuthService.login(email, password)
      .switchMap { response ->
        object : LiveData<DataState<AuthViewState>>() {
          override fun onActive() {
            super.onActive()

            when(response) {
              is ApiSuccessResponse -> {
                value = DataState.data(
                  AuthViewState(
                    authToken =  AuthToken(
                      response.body.pk,
                      response.body.token
                    )
                  ),
                  response = null
                )
              }
              is ApiErrorResponse -> {
                value = DataState.error(
                  response = Response(
                    message = response.errorMessage,
                    responseType = ResponseType.Dialog
                  )
                )
              }
              is ApiEmptyResponse -> {
                value = DataState.error(
                  response = Response(
                    message = ERROR_UNKNOWN,
                    responseType = ResponseType.Dialog
                  )
                )
              }
            }
          }
        }
      }
  }

  fun attemptRegistration(
    email: String,
    username: String,
    password: String,
    confirmPassword: String
  ): LiveData<DataState<AuthViewState>> {
    return openApiAuthService.register(email, username, password, confirmPassword)
      .switchMap { response ->
        object : LiveData<DataState<AuthViewState>>() {
          override fun onActive() {
            super.onActive()

            when(response) {
              is ApiSuccessResponse -> {
                value = DataState.data(
                  AuthViewState(
                    authToken =  AuthToken(
                      response.body.pk,
                      response.body.token
                    )
                  ),
                  response = null
                )
              }
              is ApiErrorResponse -> {
                value = DataState.error(
                  response = Response(
                    message = response.errorMessage,
                    responseType = ResponseType.Dialog
                  )
                )
              }
              is ApiEmptyResponse -> {
                value = DataState.error(
                  response = Response(
                    message = ERROR_UNKNOWN,
                    responseType = ResponseType.Dialog
                  )
                )
              }
            }
          }
        }
      }
  }
}