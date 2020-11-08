package com.takhir.openapiapp.session

import android.app.Application
import com.takhir.openapiapp.persistence.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
)
{

}