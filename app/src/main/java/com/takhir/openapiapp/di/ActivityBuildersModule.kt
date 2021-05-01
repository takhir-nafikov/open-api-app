package com.takhir.openapiapp.di

import com.takhir.openapiapp.di.auth.AuthFragmentBuildersModule
import com.takhir.openapiapp.di.auth.AuthModule
import com.takhir.openapiapp.di.auth.AuthScope
import com.takhir.openapiapp.di.auth.AuthViewModelModule
import com.takhir.openapiapp.di.main.MainFragmentBuildersModule
import com.takhir.openapiapp.di.main.MainModule
import com.takhir.openapiapp.di.main.MainScope
import com.takhir.openapiapp.di.main.MainViewModelModule
import com.takhir.openapiapp.ui.auth.AuthActivity
import com.takhir.openapiapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

  @AuthScope
  @ContributesAndroidInjector(
    modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
  )
  abstract fun contributeAuthActivity(): AuthActivity

  @MainScope
  @ContributesAndroidInjector(
    modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
  )
  abstract fun contributeMainActivity(): MainActivity
}