package com.takhir.openapiapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.databinding.FragmentRegisterBinding
import com.takhir.openapiapp.ui.auth.state.LoginFields
import com.takhir.openapiapp.ui.auth.state.RegistrationFields


class RegisterFragment : BaseAuthFragment() {

  private val binding: FragmentRegisterBinding by viewBinding(CreateMethod.INFLATE)

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
  }

  fun subscribeObservers() {
    viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
      state.registrationFields?.let { registrationFields ->
        registrationFields.registrationEmail?.let { binding.inputEmail.setText(it) }
        registrationFields.registrationUsername?.let { binding.inputUsername.setText(it) }
        registrationFields.registrationPassword?.let { binding.inputPassword.setText(it) }
        registrationFields.registrationConfirmPassword?.let { binding.inputPasswordConfirm.setText(it) }
      }
    })
  }

  override fun onDestroyView() {
    super.onDestroyView()
    viewModel.setRegistrationField(
      RegistrationFields(
        binding.inputEmail.text.toString(),
        binding.inputUsername.text.toString(),
        binding.inputPassword.text.toString(),
        binding.inputPasswordConfirm.text.toString()
      )
    )
  }
}