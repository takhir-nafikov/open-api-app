package com.takhir.openapiapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.Response
import com.takhir.openapiapp.ui.ResponseType
import com.takhir.openapiapp.util.*
import com.takhir.openapiapp.util.Constants.Companion.NETWORK_TIMEOUT
import com.takhir.openapiapp.util.Constants.Companion.TESTING_CACHE_DELAY
import com.takhir.openapiapp.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.takhir.openapiapp.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.takhir.openapiapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.takhir.openapiapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.takhir.openapiapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject,  ViewStateType>(
  isNetworkAvailable: Boolean,
  isNetworkRequest: Boolean,
  shouldCancelIfNoInternet: Boolean,
  shouldLoadFromCache: Boolean
) {

  private val TAG = "AppDebug"

  protected val result = MediatorLiveData<DataState<ViewStateType>>()
  protected lateinit var job: CompletableJob
  protected lateinit var coroutineScope: CoroutineScope

  init {
    setJob(initNewJob())
    setValue(DataState.loading(isLoading = true, cachedData = null))

    if (shouldLoadFromCache) {
      val dbSource = loadFromCache()
      result.addSource(dbSource) {
        result.removeSource(dbSource)
        setValue(DataState.loading(isLoading = true, cachedData = it))
      }
    }

    if (isNetworkRequest) {
      if (isNetworkAvailable) {
        doNetworkRequest()
      } else {
        if (shouldCancelIfNoInternet) {
          onErrorReturn(
            UNABLE_TODO_OPERATION_WO_INTERNET,
            shouldUseDialog = true,
            shouldUseToast = false
          )
        } else {
          doCacheRequest()
        }
      }
    } else {
        doCacheRequest()
    }
  }

  private fun doCacheRequest() {
    coroutineScope.launch {
      delay(TESTING_CACHE_DELAY)
      createCacheRequestAndReturn()
    }
  }

  private fun doNetworkRequest() {
    coroutineScope.launch {
      // simulate delay
      delay(TESTING_NETWORK_DELAY)

      withContext(Main) {
        // make call
        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
          result.removeSource(apiResponse)

          coroutineScope.launch {
            handleNetworkCall(response)
          }
        }
      }
    }

    GlobalScope.launch(IO) {
      delay(NETWORK_TIMEOUT)

      if (!job.isCompleted) {
        Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT.")
        job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
      }
    }
  }

  suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
    when(response){
      is ApiSuccessResponse ->{
        handleApiSuccessResponse(response)
      }
      is ApiErrorResponse ->{
        Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}" )
        onErrorReturn(response.errorMessage, true, false)
      }
      is ApiEmptyResponse ->{
        Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204)" )
        onErrorReturn("HTTP 204. Returned nothing.", true, false)
      }
    }
  }

  fun onCompleteJob(dataState: DataState<ViewStateType>) {
    GlobalScope.launch(Main) {
      job.complete()
      setValue(dataState)
    }
  }

  private fun setValue(dataState: DataState<ViewStateType>) {
    result.value = dataState
  }

  fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
    var msg = errorMessage
    var useDialog = shouldUseDialog
    var responseType: ResponseType = ResponseType.None
    if (msg == null) {
      msg = ERROR_UNKNOWN
    } else if (ErrorHandling.isNetworkError(msg)) {
      msg = ERROR_CHECK_NETWORK_CONNECTION
      useDialog = false
    }

    if (shouldUseToast) {
      responseType = ResponseType.Toast
    }
    if (useDialog) {
      responseType = ResponseType.Dialog
    }

    onCompleteJob(DataState.error(
      response = Response(
        message =  msg,
        responseType = responseType
      )
    ))
  }

  @OptIn(InternalCoroutinesApi::class)
  private fun initNewJob(): Job {
    Log.d(TAG, "initNewJob: called...")
    job = Job()
    job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object : CompletionHandler {

      override fun invoke(cause: Throwable?) {
        if (job.isCancelled) {
          Log.e(TAG, "NetworkBoundResource: Job has been cancelled." )
          onErrorReturn(cause?.message ?: ERROR_UNKNOWN, false, true)
        } else if (job.isCompleted) {
          Log.e(TAG, "NetworkBoundResource: Job has been completed." )
          // Do nothing
        }
      }

    })

    coroutineScope = CoroutineScope(IO + job)
    return job
  }

  fun asLiveData() = result as LiveData<DataState<ViewStateType>>

  abstract suspend fun createCacheRequestAndReturn()

  abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

  abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

  abstract fun loadFromCache(): LiveData<ViewStateType>

  abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

  abstract fun setJob(job: Job)
}