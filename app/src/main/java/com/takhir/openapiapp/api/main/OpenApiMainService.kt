package com.takhir.openapiapp.api.main

import androidx.lifecycle.LiveData
import com.takhir.openapiapp.api.GenericResponse
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


}