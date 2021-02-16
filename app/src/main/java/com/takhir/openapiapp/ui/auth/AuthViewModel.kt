package com.takhir.openapiapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.takhir.openapiapp.api.auth.network_responses.LoginResponse
import com.takhir.openapiapp.api.auth.network_responses.RegistrationResponse
import com.takhir.openapiapp.repository.auth.AuthRepository
import com.takhir.openapiapp.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
  val authRepository: AuthRepository
) : ViewModel()
{

  fun testLogin(): LiveData<GenericApiResponse<LoginResponse>> {
    return authRepository.testLoginRequest(
      "qazwsxedc@yandex.ru",
      "qazwsxedc1"
    )
  }

  fun testReg(): LiveData<GenericApiResponse<RegistrationResponse>> {
    return authRepository.testRegRequest(
      "qazwsxedc@yandex.ru",
      "qazwsxedc1",
      "qazwsxedc1",
      "qazwsxedc1"
    )
  }
}