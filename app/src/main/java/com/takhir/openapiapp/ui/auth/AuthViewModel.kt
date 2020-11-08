package com.takhir.openapiapp.ui.auth

import androidx.lifecycle.ViewModel
import com.takhir.openapiapp.repository.auth.AuthRepository

class AuthViewModel
constructor(
  val authRepository: AuthRepository
) : ViewModel()
{
}