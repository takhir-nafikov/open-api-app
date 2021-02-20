package com.takhir.openapiapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.takhir.openapiapp.R
import com.takhir.openapiapp.util.ApiEmptyResponse
import com.takhir.openapiapp.util.ApiErrorResponse
import com.takhir.openapiapp.util.ApiSuccessResponse


class RegisterFragment : BaseAuthFragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_register, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.testReg().observe(viewLifecycleOwner, Observer { response ->

      when(response){
        is ApiSuccessResponse ->{
          Log.d(TAG, "REGISTER RESPONSE: ${response.body}")
        }
        is ApiErrorResponse ->{
          Log.d(TAG, "REGISTER RESPONSE: ${response.errorMessage}")
        }
        is ApiEmptyResponse ->{
          Log.d(TAG, "REGISTER RESPONSE: Empty Response")
        }
      }
    })
  }
}