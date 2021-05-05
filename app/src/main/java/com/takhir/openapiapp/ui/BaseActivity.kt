package com.takhir.openapiapp.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.takhir.openapiapp.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(),
  DataStateChangeListener
{

  val TAG = "AppDebug"

  @Inject
  lateinit var sessionManager: SessionManager

  override fun onDataStateChange(dataState: DataState<*>?) {
    dataState?.let { state ->
      GlobalScope.launch(Main) {
        displayProgressBar(state.loading.isLoading)

        state.error?.let { errorEvent ->
          handleStateError(errorEvent)
        }

        state.data?.let {

          it.response?.let { responseEvent ->
            handleStateResponse(responseEvent)
          }
        }
      }
    }
  }

  private fun handleStateResponse(responseEvent: Event<Response>) {
    responseEvent.getContentIfNotHandled()?.let {
      when(it.responseType) {
        is ResponseType.Toast -> {
          it.message?.let { message ->
            displayToast(message)
          }
        }
        is ResponseType.Dialog -> {
          it.message?.let { message ->
            displaySuccessDialog(message)
          }
        }
        is ResponseType.None -> {
          Log.d(TAG, "handleStateResponse: ${it.message}" )
        }
      }
    }
  }

  private fun handleStateError(errorEvent: Event<StateError>) {
    errorEvent.getContentIfNotHandled()?.let {
      when(it.response.responseType) {
        is ResponseType.Toast -> {
          it.response.message?.let { message ->
            displayToast(message)
          }
        }
        is ResponseType.Dialog -> {
          it.response.message?.let { message ->
            displayErrorDialog(message)
          }
        }
        is ResponseType.None -> {
          Log.e(TAG, "handleStateError: ${it.response.message}" )
        }
      }
    }
  }

  override fun hideSoftKeyboard() {
    if (currentFocus != null) {
      val inputManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

      inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)

    }
  }

  abstract fun displayProgressBar(boolean: Boolean)
}