package com.takhir.openapiapp.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.takhir.openapiapp.R
import com.takhir.openapiapp.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment

abstract class BaseCreateBlogFragment : DaggerFragment() {

  val TAG: String = "AppDebug"

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
    setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)

    cancelActiveJobs()
  }

  fun cancelActiveJobs() {
//    viewModel.cancelActiveJobs()
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