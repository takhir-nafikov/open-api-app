package com.takhir.openapiapp.ui.main.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.takhir.openapiapp.R
import com.takhir.openapiapp.ui.DataStateChangeListener
import com.takhir.openapiapp.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseAccountFragment : DaggerFragment() {

  val TAG: String = "AppDebug"

  @Inject
  lateinit var providerFactory: ViewModelProviderFactory

  lateinit var viewModel: AccountViewModel

  lateinit var stateChangeListener: DataStateChangeListener

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      stateChangeListener = context as DataStateChangeListener
    } catch (e: ClassCastException) {
      Log.e(TAG, "$context must implement DataStateChangeListener")
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

    viewModel = activity?.run {
      ViewModelProvider(this, providerFactory).get(AccountViewModel::class.java)
    } ?: throw Exception("Invalid activity")

    cancelActiveJobs()
  }

  fun cancelActiveJobs() {
    viewModel.cancelActiveJobs()
  }

  private fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
    val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
    NavigationUI.setupActionBarWithNavController(
      activity,
      findNavController(),
      appBarConfiguration
    )
  }
}