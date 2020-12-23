package com.takhir.openapiapp.di

import androidx.lifecycle.ViewModelProvider
import com.takhir.openapiapp.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}