package com.takhir.openapiapp.api.main

import androidx.lifecycle.LiveData
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {

  @GET("account/properties")
  fun getAccountProperties(
    @Header("Authorization") authorization: String
  ): LiveData<GenericApiResponse<AccountProperties>>

}