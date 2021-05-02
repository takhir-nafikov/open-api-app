package com.takhir.openapiapp.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.FragmentViewBlogBinding

class ViewBlogFragment : BaseBlogFragment(){

  private val binding: FragmentViewBlogBinding by viewBinding(CreateMethod.INFLATE)

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
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    // TODO("Check if user is author of blog post")
    val isAuthorOfBlogPost = true
    if(isAuthorOfBlogPost){
      inflater.inflate(R.menu.edit_view_menu, menu)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // TODO("Check if user is author of blog post")
    val isAuthorOfBlogPost = true
    if(isAuthorOfBlogPost) {
      when (item.itemId) {
        R.id.edit -> {
          navUpdateBlogFragment()
          return true
        }
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun navUpdateBlogFragment(){
    findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
  }
}