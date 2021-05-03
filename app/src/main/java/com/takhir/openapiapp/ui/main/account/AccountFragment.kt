package com.takhir.openapiapp.ui.main.account

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.FragmentAccountBinding
import com.takhir.openapiapp.session.SessionManager
import javax.inject.Inject

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