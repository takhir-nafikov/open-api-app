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


class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.testLogin().observe(viewLifecycleOwner, Observer {
            when(it) {
                is ApiSuccessResponse -> {
                    Log.d(TAG, "LOGIN RESPONSE: ${it.body}")
                }
                is ApiErrorResponse -> {
                    Log.d(TAG, "LOGIN RESPONSE: ${it.errorMessage}")
                }
                is ApiEmptyResponse -> {
                    Log.d(TAG, "LOGIN RESPONSE: Empty Response")
                }
            }
        })
    }
}