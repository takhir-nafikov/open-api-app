package com.takhir.openapiapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.databinding.ActivityMainBinding
import com.takhir.openapiapp.ui.BaseActivity
import com.takhir.openapiapp.ui.auth.AuthActivity

class MainActivity : BaseActivity() {

  private val  binding: ActivityMainBinding by viewBinding(CreateMethod.INFLATE)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    binding.toolBar.setOnClickListener {
      sessionManager.logout()
    }

    subscribeObservers()
  }

  fun subscribeObservers() {
    sessionManager.cachedToken.observe(this, Observer { authToken ->
      Log.d(TAG, "MainActivity: subscribeObservers: AuthToken $authToken")
      if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
        navAuthActivity()
      }
    })
  }

  override fun displayProgressBar(boolean: Boolean) {
    binding.progressBar.isVisible = boolean
  }

  private fun navAuthActivity() {
    val intent = Intent(this, AuthActivity::class.java)
    startActivity(intent)
    finish()
  }
}