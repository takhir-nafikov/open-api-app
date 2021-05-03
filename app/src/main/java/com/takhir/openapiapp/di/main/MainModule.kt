package com.takhir.openapiapp.di.main

import com.takhir.openapiapp.api.main.OpenApiMainService
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.repository.main.AccountRepository
import com.takhir.openapiapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

  @MainScope
  @Provides
  fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
    return retrofitBuilder
      .build()
      .create(OpenApiMainService::class.java)
  }

  @MainScope
  @Provides
  fun provideMainRepository(
    openApiMainService: OpenApiMainService,
    accountPropertiesDao: AccountPropertiesDao,
    sessionManager: SessionManager
  ): AccountRepository {
    return AccountRepository(
      openApiMainService,
      accountPropertiesDao,
      sessionManager
    )
  }
}