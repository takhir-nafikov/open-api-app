package com.takhir.openapiapp.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.RequestManager
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.FragmentBlogBinding
import com.takhir.openapiapp.models.BlogPost
import com.takhir.openapiapp.ui.main.blog.state.BlogSearchEvent
import com.takhir.openapiapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject

class BlogFragment : BaseBlogFragment(),
  BlogListAdapter.Interaction
{

  private lateinit var recyclerAdapter: BlogListAdapter

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

    initRecyclerView()
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
      if (viewState != null) {
        recyclerAdapter.submitList(
          list = viewState.blogFields.blogList,
          isQueryExhausted = true
        )
      }
    })
  }

  private fun initRecyclerView() {
    binding.blogPostRecyclerview.apply {
      layoutManager = LinearLayoutManager(this@BlogFragment.context)
      val topSpacingItemDecoration = TopSpacingItemDecoration(30)
      removeItemDecoration(topSpacingItemDecoration)
      addItemDecoration(topSpacingItemDecoration)

      recyclerAdapter = BlogListAdapter(
        requestManager = requestManager,
        interaction = this@BlogFragment
      )

      addOnScrollListener(object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
          super.onScrollStateChanged(recyclerView, newState)
          val layoutManager = recyclerView.layoutManager as LinearLayoutManager
          val lastPosition = layoutManager.findLastVisibleItemPosition()
          if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
            Log.d(TAG, "BlogFragment, onScrollStateChanged: attempting to load next page")
            // TODO("load next page")
          }
        }
      })

      adapter = recyclerAdapter
    }
  }

  override fun onDestroyView() {
    binding.blogPostRecyclerview.adapter = null

    super.onDestroyView()
  }

  override fun onItemSelected(position: Int, item: BlogPost) {
    viewModel.setBlogPost(item)
    findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
  }

}