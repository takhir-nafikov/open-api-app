package com.takhir.openapiapp.api.main

import androidx.lifecycle.LiveData
import com.takhir.openapiapp.api.GenericResponse
import com.takhir.openapiapp.api.main.responses.BlogListSearchResponse
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.util.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {

  @GET("account/properties")
  fun getAccountProperties(
    @Header("Authorization") authorization: String
  ): LiveData<GenericApiResponse<AccountProperties>>

  @PUT("account/properties/update")
  @FormUrlEncoded
  fun saveAccountProperties(
    @Header("Authorization") authorization: String,
    @Field("email") email: String,
    @Field("username") username: String
  ): LiveData<GenericApiResponse<GenericResponse>>

  @PUT("account/change_password/")
  @FormUrlEncoded
  fun updatePassword(
    @Header("Authorization") authorization: String,
    @Field("old_password") currentPassword: String,
    @Field("new_password") newPassword: String,
    @Field("confirm_new_password") confirmNewPassword: String
  ): LiveData<GenericApiResponse<GenericResponse>>

  @GET("blog/list")
  fun searchListBlogPosts(
    @Header("Authorization") authorization: String,
    @Query("search") query: String
  ): LiveData<GenericApiResponse<BlogListSearchResponse>>
}