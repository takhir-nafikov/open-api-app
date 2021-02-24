package com.takhir.openapiapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.databinding.FragmentLoginBinding
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.ui.auth.state.AuthStateEvent
import com.takhir.openapiapp.ui.auth.state.LoginAttemptEvent
import com.takhir.openapiapp.ui.auth.state.LoginFields


class LoginFragment : BaseAuthFragment() {

  private val binding: FragmentLoginBinding by viewBinding(CreateMethod.INFLATE)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return binding.root
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    subscribeObservers()

    binding.loginButton.setOnClickListener {
      login()
    }
  }

  fun subscribeObservers() {
    viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
      state.loginFields?.let { loginFields ->
        loginFields.loginEmail?.let { binding.inputEmail.setText(it) }
        loginFields.loginPassword?.let { binding.inputPassword.setText(it) }
      }
    })
  }

  fun login() {
    viewModel.setStateEvent(
      LoginAttemptEvent(
        binding.inputEmail.text.toString(),
        binding.inputPassword.text.toString()
      )
    )
  }

  override fun onDestroyView() {
    super.onDestroyView()
    viewModel.setLoginFields(
      LoginFields(
        binding.inputEmail.text.toString(),
        binding.inputPassword.text.toString()
      )
    )
  }
}