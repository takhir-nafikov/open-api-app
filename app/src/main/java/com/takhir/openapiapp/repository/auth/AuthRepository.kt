package com.takhir.openapiapp.repository.auth

import com.takhir.openapiapp.api.auth.OpenApiAuthService
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.persistence.AuthTokenDao
import com.takhir.openapiapp.session.SessionManager
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
}