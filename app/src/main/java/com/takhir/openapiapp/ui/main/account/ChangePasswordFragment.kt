package com.takhir.openapiapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.FragmentChangePasswordBinding
import com.takhir.openapiapp.ui.main.account.state.ChangePasswordEvent
import com.takhir.openapiapp.util.SuccessHandling.Companion.RESPONSE_PASSWORD_UPDATE_SUCCESS

class ChangePasswordFragment : BaseAccountFragment() {

  private val binding: FragmentChangePasswordBinding by viewBinding(CreateMethod.INFLATE)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.updatePasswordButton.setOnClickListener {
      viewModel.setStateEvent(
        ChangePasswordEvent(
          binding.inputCurrentPassword.text.toString(),
          binding.inputNewPassword.text.toString(),
          binding.inputConfirmNewPassword.text.toString()
        )
      )
    }

    subscribeObservers()
  }

  private fun subscribeObservers() {
    viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
      stateChangeListener.onDataStateChange(dataState)
      Log.d(TAG, "ChangePasswordFragment, subscribeObservers: DataState $dataState")
      dataState?.data?.response?.let { event ->
        if (event.peekContent()
            .message
            .equals(RESPONSE_PASSWORD_UPDATE_SUCCESS)) {
          stateChangeListener.hideSoftKeyboard()
          findNavController().popBackStack()
        }
      }
    })
  }
}