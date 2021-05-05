package com.takhir.openapiapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.FragmentUpdateAccountBinding
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.ui.main.account.state.UpdateAccountPropertiesEvent

class UpdateAccountFragment : BaseAccountFragment() {

  private val binding: FragmentUpdateAccountBinding by viewBinding(CreateMethod.INFLATE)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setHasOptionsMenu(true)

    subscribeObservers()
  }

  private fun subscribeObservers() {
    viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState->
      stateChangeListener.onDataStateChange(dataState)
      Log.d(TAG, "UpdateAccountFragment, subscribeObservers: $dataState")
    })

    viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
      viewState?.accountProperties?.let {
        Log.d(TAG, "UpdateAccountFragment, subscribeObservers: $it")
        setAccountDataField(it)
      }
    })
  }

  private fun setAccountDataField(accountProperties: AccountProperties) {
    binding.inputEmail.setText(accountProperties.email)
    binding.inputUsername.setText(accountProperties.username)
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.update_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {

    when(item.itemId) {
      R.id.save -> {
        saveChanges()
        return true
      }
    }

    return super.onOptionsItemSelected(item)
  }

  private fun saveChanges() {
    viewModel.setStateEvent(
      UpdateAccountPropertiesEvent(
        binding.inputEmail.text.toString(),
        binding.inputUsername.text.toString()
      )
    )
    stateChangeListener.hideSoftKeyboard()
  }

}