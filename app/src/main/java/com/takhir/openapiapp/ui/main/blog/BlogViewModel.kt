package com.takhir.openapiapp.ui.main.blog

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.takhir.openapiapp.models.BlogPost
import com.takhir.openapiapp.repository.main.BlogRepository
import com.takhir.openapiapp.session.SessionManager
import com.takhir.openapiapp.ui.BaseViewModel
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.main.blog.state.BlogSearchEvent
import com.takhir.openapiapp.ui.main.blog.state.BlogStateEvent
import com.takhir.openapiapp.ui.main.blog.state.BlogViewState
import com.takhir.openapiapp.ui.main.blog.state.None
import com.takhir.openapiapp.util.AbsentLiveData
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
  private val sessionManager: SessionManager,
  private val blogRepository: BlogRepository,
  private val sharedPreferences: SharedPreferences,
  private val requestManager: RequestManager
): BaseViewModel<BlogStateEvent, BlogViewState>() {

  override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
    when(stateEvent) {

      is BlogSearchEvent -> {
        return AbsentLiveData.create()
      }

      is None -> {
        return AbsentLiveData.create()
      }
    }

  }

  override fun initNewViewState(): BlogViewState {
    return BlogViewState()
  }

  fun setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    if (query.equals(update.blogFields.searchQuery)) {
      return
    }
    update.blogFields.searchQuery = query
    _viewState.value = update
  }

  fun setBlogList(blogList: List<BlogPost>) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.blogList = blogList
    _viewState.value = update
  }

  fun cancelActiveJobs() {
    blogRepository.cancelActiveJobs()
    handlePendingData()
  }

  private fun handlePendingData() {
    setStateEvent(None)
  }

  override fun onCleared() {
    super.onCleared()
    cancelActiveJobs()
  }
}