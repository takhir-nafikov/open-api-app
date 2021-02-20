package com.takhir.openapiapp.di.auth

import androidx.lifecycle.ViewModel
import com.takhir.openapiapp.di.ViewModelKey
import com.takhir.openapiapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(AuthViewModel::class)
  abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}