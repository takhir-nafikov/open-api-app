package com.takhir.openapiapp.session

import android.app.Application
import com.takhir.openapiapp.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
  val authTokenDao: AuthTokenDao,
  val application: Application
)
{

}