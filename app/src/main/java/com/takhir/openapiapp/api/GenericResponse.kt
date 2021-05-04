package com.takhir.openapiapp.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenericResponse(

  @SerializedName("response")
  @Expose
  var response: String
)