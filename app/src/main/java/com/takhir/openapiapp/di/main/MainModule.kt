package com.takhir.openapiapp.di.main

import com.takhir.openapiapp.api.main.OpenApiMainService
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.persistence.AppDatabase
import com.takhir.openapiapp.persistence.BlogPostDao
import com.takhir.openapiapp.repository.main.AccountRepository
import com.takhir.openapiapp.repository.main.BlogRepository
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
  fun provideAccountRepository(
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

  @MainScope
  @Provides
  fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
    return db.getBlogPostDao()
  }

  @MainScope
  @Provides
  fun provideBlogRepository(
    openApiMainService: OpenApiMainService,
    blogPostDao: BlogPostDao,
    sessionManager: SessionManager
  ): BlogRepository {
    return BlogRepository(
      openApiMainService,
      blogPostDao,
      sessionManager)
  }
}