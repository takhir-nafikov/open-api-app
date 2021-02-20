package com.takhir.openapiapp.repository.auth

import androidx.lifecycle.LiveData
import com.takhir.openapiapp.api.auth.OpenApiAuthService
import com.takhir.openapiapp.api.auth.network_responses.LoginResponse
import com.takhir.openapiapp.api.auth.network_responses.RegistrationResponse
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.persistence.AuthTokenDao
import com.takhir.openapiapp.session.SessionManager
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

  fun testLoginRequest(email: String, password: String): LiveData<GenericApiResponse<LoginResponse>> {
    return openApiAuthService.login(email, password)
  }

  fun testRegRequest(
    email: String,
    username: String,
    password: String,
    confirmPassword: String
  ): LiveData<GenericApiResponse<RegistrationResponse>> {
    return openApiAuthService.register(email, username, password, confirmPassword)
  }
}