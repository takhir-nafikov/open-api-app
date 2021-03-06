package com.takhir.openapiapp.di.auth

import android.content.SharedPreferences
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
  fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
    return retrofitBuilder
      .build()
      .create(OpenApiAuthService::class.java)
  }

  @AuthScope
  @Provides
  fun provideAuthRepository(
    sessionManager: SessionManager,
    authTokenDao: AuthTokenDao,
    accountPropertiesDao: AccountPropertiesDao,
    openApiAuthService: OpenApiAuthService,
    sharedPreferences: SharedPreferences,
    editor: SharedPreferences.Editor
  ): AuthRepository {
    return AuthRepository(
      authTokenDao,
      accountPropertiesDao,
      openApiAuthService,
      sessionManager,
      sharedPreferences,
      editor
    )
  }
}