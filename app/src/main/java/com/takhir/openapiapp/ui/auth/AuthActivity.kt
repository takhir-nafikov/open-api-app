package com.takhir.openapiapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.ActivityAuthBinding
import com.takhir.openapiapp.ui.BaseActivity
import com.takhir.openapiapp.ui.ResponseType
import com.takhir.openapiapp.ui.main.MainActivity
import com.takhir.openapiapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity(),  NavController.OnDestinationChangedListener {

  @Inject
  lateinit var providerFactory: ViewModelProviderFactory

  lateinit var viewModel: AuthViewModel

  private val  binding: ActivityAuthBinding by viewBinding(CreateMethod.INFLATE)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
    findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)

    subscribeObservers()
  }

  override fun onDestinationChanged(
    controller: NavController,
    destination: NavDestination,
    arguments: Bundle?
  ) {
    viewModel.cancelActiveJobs()
  }

  override fun displayProgressBar(boolean: Boolean) {
    binding.progressBar.isVisible = boolean
  }

  private fun subscribeObservers() {
    viewModel.dataState.observe(this, Observer { dataState ->
      onDataStateChange(dataState)
      dataState.data?.let { data ->

        data.data?.let { event ->
          event.getContentIfNotHandled()?.let { authViewState -> 
            authViewState.authToken?.let {
              Log.d(TAG, "AuthActivity, DataState: $it")
              viewModel.setAuthToken(it)
            }
          }
        }
      }
    })

    viewModel.viewState.observe(this, Observer {
      it.authToken?.let { authToken ->
        sessionManager.login(authToken)
      }
    })

    sessionManager.cachedToken.observe(this, Observer { authToken ->
      Log.d(TAG, "AuthActivity: subscribeObservers: AuthToken $authToken")
      if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
        navMainActivity()
      }
    })
  }

  private fun navMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }
}