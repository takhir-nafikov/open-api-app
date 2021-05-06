package com.takhir.openapiapp.ui.main.blog

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
import com.takhir.openapiapp.databinding.FragmentBlogBinding
import com.takhir.openapiapp.ui.main.blog.state.BlogSearchEvent
import kotlinx.android.synthetic.main.fragment_blog.*

class BlogFragment : BaseBlogFragment(){

  private val binding: FragmentBlogBinding by viewBinding(CreateMethod.INFLATE)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    goViewBlogFragment.setOnClickListener {
      findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    subscribeObservers()
    executeSearch()
  }

  private fun executeSearch(){
    viewModel.setQuery("")
    viewModel.setStateEvent(BlogSearchEvent)
  }

  private fun subscribeObservers(){
    viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
      if(dataState != null) {
        stateChangeListener.onDataStateChange(dataState)
        dataState.data?.let {
          it.data?.let { event ->
            event.getContentIfNotHandled()?.let { viewState ->
              Log.d(TAG, "BlogFragment, DataState: $viewState")
              viewModel.setBlogList(viewState.blogFields.blogList)
            }
          }
        }
      }
    })

    viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
      Log.d(TAG, "BlogFragment, ViewState: $viewState")

    })
  }

}