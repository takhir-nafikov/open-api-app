package com.takhir.openapiapp.di.main

import androidx.lifecycle.ViewModel
import com.takhir.openapiapp.di.ViewModelKey
import com.takhir.openapiapp.ui.main.account.AccountViewModel
import com.takhir.openapiapp.ui.main.blog.BlogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(AccountViewModel::class)
  abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(BlogViewModel::class)
  abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel
}