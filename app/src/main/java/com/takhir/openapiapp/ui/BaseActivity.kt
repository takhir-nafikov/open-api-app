package com.takhir.openapiapp.ui

import com.takhir.openapiapp.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

  val TAG = "AppDebug"

  @Inject
  lateinit var sessionManager: SessionManager

}