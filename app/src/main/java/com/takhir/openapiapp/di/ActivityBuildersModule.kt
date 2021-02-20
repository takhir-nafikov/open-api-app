package com.takhir.openapiapp.di

import com.takhir.openapiapp.di.auth.AuthFragmentBuildersModule
import com.takhir.openapiapp.di.auth.AuthModule
import com.takhir.openapiapp.di.auth.AuthScope
import com.takhir.openapiapp.di.auth.AuthViewModelModule
import com.takhir.openapiapp.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

  @AuthScope
  @ContributesAndroidInjector(
    modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
  )
  abstract fun contributeAuthActivity(): AuthActivity
}