package com.takhir.openapiapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.ActivityMainBinding
import com.takhir.openapiapp.ui.BaseActivity
import com.takhir.openapiapp.ui.auth.AuthActivity
import com.takhir.openapiapp.ui.main.account.ChangePasswordFragment
import com.takhir.openapiapp.ui.main.account.UpdateAccountFragment
import com.takhir.openapiapp.ui.main.blog.UpdateBlogFragment
import com.takhir.openapiapp.ui.main.blog.ViewBlogFragment
import com.takhir.openapiapp.util.BottomNavController
import com.takhir.openapiapp.util.setUpNavigation
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : BaseActivity(),
  BottomNavController.NavGraphProvider,
  BottomNavController.OnNavigationGraphChanged,
  BottomNavController.OnNavigationReselectedListener
{

  private val  binding: ActivityMainBinding by viewBinding(CreateMethod.INFLATE)

  private lateinit var bottomNavigationView: BottomNavigationView

  private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
    BottomNavController(
      this,
      R.id.main_nav_host_fragment,
      R.id.nav_blog,
      this,
      this
    )
  }

  override fun getNavGraphId(itemId: Int) = when(itemId) {

    R.id.nav_blog -> {
      R.navigation.nav_blog
    }

    R.id.nav_account -> {
      R.navigation.nav_account
    }

    R.id.nav_create_blog -> {
      R.navigation.nav_create_blog
    }

    else -> {
      R.navigation.nav_blog
    }
  }

  override fun onGraphChanged() {}

  override fun onReselectNavItem(
    navController: NavController,
    fragment: Fragment
  ) = when(fragment) {

    is ViewBlogFragment -> {
      navController.navigate(R.id.action_viewBlogFragment_to_home)
    }

    is UpdateBlogFragment -> {
      navController.navigate(R.id.action_updateBlogFragment_to_home)
    }

    is UpdateAccountFragment -> {
      navController.navigate(R.id.action_updateAccountFragment_to_home)
    }

    is ChangePasswordFragment -> {
      navController.navigate(R.id.action_changePasswordFragment_to_home)
    }

    else -> {
      // do nothing
    }
  }

  override fun onBackPressed() = bottomNavController.onBackPressed()

  override fun onOptionsItemSelected(item: MenuItem): Boolean {

    when(item.itemId){
      android.R.id.home -> onBackPressed()
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setupActionBar()
    bottomNavigationView = findViewById(R.id.bottom_navigation_view)
    bottomNavigationView.setUpNavigation(bottomNavController, this)
    if (savedInstanceState == null) {
      bottomNavController.onNavigationItemSelected()
    }

    subscribeObservers()
  }

  fun subscribeObservers() {
    sessionManager.cachedToken.observe(this, Observer { authToken ->
      Log.d(TAG, "MainActivity: subscribeObservers: AuthToken $authToken")
      if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
        navAuthActivity()
        finish()
      }
    })
  }

  override fun displayProgressBar(boolean: Boolean) {
    binding.progressBar.isVisible = boolean
  }

  private fun setupActionBar(){
    setSupportActionBar(binding.toolBar)
  }

  private fun navAuthActivity() {
    val intent = Intent(this, AuthActivity::class.java)
    startActivity(intent)
    finish()
  }
}