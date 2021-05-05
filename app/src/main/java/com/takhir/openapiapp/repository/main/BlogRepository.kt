package com.takhir.openapiapp.repository.main

import com.takhir.openapiapp.api.main.OpenApiMainService
import com.takhir.openapiapp.persistence.BlogPostDao
import com.takhir.openapiapp.repository.JobManager
import com.takhir.openapiapp.session.SessionManager
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
  val openApiMainService: OpenApiMainService,
  val blogPostDao: BlogPostDao,
  val sessionManager: SessionManager
): JobManager("BlogRepository") {

  private val TAG = "AppDebug"
}