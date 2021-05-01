package com.takhir.openapiapp.di.main

import com.takhir.openapiapp.ui.main.account.AccountFragment
import com.takhir.openapiapp.ui.main.account.ChangePasswordFragment
import com.takhir.openapiapp.ui.main.account.UpdateAccountFragment
import com.takhir.openapiapp.ui.main.blog.BlogFragment
import com.takhir.openapiapp.ui.main.blog.UpdateBlogFragment
import com.takhir.openapiapp.ui.main.blog.ViewBlogFragment
import com.takhir.openapiapp.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

  @ContributesAndroidInjector()
  abstract fun contributeBlogFragment(): BlogFragment

  @ContributesAndroidInjector()
  abstract fun contributeAccountFragment(): AccountFragment

  @ContributesAndroidInjector()
  abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

  @ContributesAndroidInjector()
  abstract fun contributeCreateBlogFragment(): CreateBlogFragment

  @ContributesAndroidInjector()
  abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

  @ContributesAndroidInjector()
  abstract fun contributeViewBlogFragment(): ViewBlogFragment

  @ContributesAndroidInjector()
  abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}