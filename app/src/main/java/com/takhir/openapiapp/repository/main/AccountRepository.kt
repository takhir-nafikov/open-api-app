package com.takhir.openapiapp.repository.main

import android.util.Log
import com.takhir.openapiapp.api.main.OpenApiMainService
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.session.SessionManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
  val openApiMainService: OpenApiMainService,
  val accountPropertiesDao: AccountPropertiesDao,
  val sessionManager: SessionManager
) {
  private val TAG = "AppDebug"

  private var repositoryJob: Job? = null

  fun cancelActiveJobs() {
    Log.d(TAG, "AccountRepository: cancelActiveJobs")
  }
}