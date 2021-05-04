package com.takhir.openapiapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.FragmentAccountBinding
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.ui.main.account.state.GetAccountPropertiesEvent

class AccountFragment : BaseAccountFragment() {

  private val binding: FragmentAccountBinding by viewBinding(CreateMethod.INFLATE)


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

    binding.changePassword.setOnClickListener{
      findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
    }

    binding.logoutButton.setOnClickListener {
      viewModel.logout()
    }

    subscribeObservers()
  }

  // TODO подумаать как убрать кучу let
  private fun subscribeObservers() {
    viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
      stateChangeListener.onDataStateChange(dataState)
      dataState?.let {
        it.data?.let { data ->
          data.data?.let { event ->
            event.getContentIfNotHandled()?.let { viewState ->
              viewState.accountProperties?.let { accountProperties ->
                Log.d(TAG, "AccountFragment-subscribeObservers: $accountProperties")
                viewModel.setAccountPropertiesData(accountProperties)
              }
            }
          }
        }
      }
    })
    
    viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState -> 
      viewState?.let {
        it.accountProperties?.let { accountProperties ->
          Log.d(TAG, "AccountFragment-subscribeObservers: $accountProperties")
          setAccountDataField(accountProperties)
        }
      }
    })
  }

  override fun onResume() {
    super.onResume()
    viewModel.setStateEvent(
      GetAccountPropertiesEvent
    )
  }

  private fun setAccountDataField(accountProperties: AccountProperties) {
    binding.email.text = accountProperties.email
    binding.username.text = accountProperties.username
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.edit_view_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when(item.itemId) {
      R.id.edit -> {
        findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }
}