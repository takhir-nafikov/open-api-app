package com.takhir.openapiapp.di.auth

import com.takhir.openapiapp.api.auth.OpenApiAuthService
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.persistence.AuthTokenDao
import com.takhir.openapiapp.repository.auth.AuthRepository
import com.takhir.openapiapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideFakeApiService(): OpenApiAuthService {
        return Retrofit.Builder()
            .baseUrl("https://open-api.xyz")
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }
}