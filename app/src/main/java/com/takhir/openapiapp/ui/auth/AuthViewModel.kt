package com.takhir.openapiapp.ui.auth

import androidx.lifecycle.ViewModel
import com.takhir.openapiapp.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
  val authRepository: AuthRepository
) : ViewModel()
{
}