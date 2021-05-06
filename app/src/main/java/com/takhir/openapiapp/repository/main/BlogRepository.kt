package com.takhir.openapiapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.takhir.openapiapp.api.main.OpenApiMainService
import com.takhir.openapiapp.api.main.responses.BlogListSearchResponse
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.models.BlogPost
import com.takhir.openapiapp.persistence.BlogPostDao
import com.takhir.openapiapp.repository.JobManager
import com.takhir.openapiapp.repository.NetworkBoundResource
import com.takhir.openapiapp.session.SessionManager
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.main.blog.state.BlogViewState
import com.takhir.openapiapp.util.ApiSuccessResponse
import com.takhir.openapiapp.util.DateUtils
import com.takhir.openapiapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
  val openApiMainService: OpenApiMainService,
  val blogPostDao: BlogPostDao,
  val sessionManager: SessionManager
): JobManager("BlogRepository") {

  private val TAG = "AppDebug"

  fun searchBlogPosts(
    authToken: AuthToken,
    query: String
  ): LiveData<DataState<BlogViewState>> {
    return object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
      isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
      isNetworkRequest = true,
      shouldCancelIfNoInternet = false,
      shouldLoadFromCache = true
    ) {
      override suspend fun createCacheRequestAndReturn() {
        withContext(Main) {
          result.addSource(loadFromCache()) { viewState ->
            onCompleteJob(DataState.data(viewState, null))
          }
        }
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {
        val blogPostList: ArrayList<BlogPost> = ArrayList()
        for(blogPostResponse in response.body.results) {
          blogPostList.add(
            BlogPost(
              pk = blogPostResponse.pk,
              title = blogPostResponse.title,
              slug = blogPostResponse.slug,
              body = blogPostResponse.body,
              image = blogPostResponse.image,
              date_updated = DateUtils.convertServerStringDateToLong(
                blogPostResponse.date_updated
              ),
              username = blogPostResponse.username
            )
          )
        }
        updateLocalDb(blogPostList)

        createCacheRequestAndReturn()
      }

      override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
        return openApiMainService.searchListBlogPosts(
          "Token ${authToken.token!!}",
          query = query
        )
      }

      override fun loadFromCache(): LiveData<BlogViewState> {
        return blogPostDao.getAllBlogPosts()
          .switchMap {
            object: LiveData<BlogViewState>() {
              override fun onActive() {
                super.onActive()
                value = BlogViewState(
                  BlogViewState.BlogFields(
                    blogList = it
                  )
                )
              }
            }
          }
      }

      override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
       if (cacheObject != null) {
        withContext(IO) {
          for (blogPost in cacheObject) {
            try {
              // launch each insert as a separate job
              val j = launch {
                Log.d(TAG, "BlogRepository, updateLocalDb: inserting blog: $blogPost")
                blogPostDao.insert(blogPost)
              }
            } catch (e: Exception) {
              Log.e(TAG, "BlogRepository, updateLocalDb: error updating cache " +
                  "on blog post with slug ${blogPost.slug}")
            }
          }
        }
       }
      }

      override fun setJob(job: Job) {
        addJob("searchBlogPosts", job)
      }
    }.asLiveData()
  }
}