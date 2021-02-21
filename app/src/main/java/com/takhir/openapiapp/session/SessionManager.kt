package com.takhir.openapiapp.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.persistence.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
  val authTokenDao: AuthTokenDao,
  val application: Application
)
{
  private val TAG = "AppDebug"

  private val _cacheToken = MutableLiveData<AuthToken>()

  val cachedToken: LiveData<AuthToken>
    get() = _cacheToken

  fun login(newValue: AuthToken) {
    setValue(newValue)
  }

  fun logout() {
    Log.d(TAG, "logout: ...")

    GlobalScope.launch(IO) {
      var errorMessage: String? = null
      try {
        cachedToken.value!!.account_pk?.let {
          authTokenDao.nullifyToken(it)
        }
      } catch (e: CancellationException) {
        Log.e(TAG, "logout: ${e.message}")
        errorMessage = e.message
      } catch (e: Exception) {
        Log.e(TAG, "logout: ${e.message}")
        errorMessage += "\n ${e.message}"
      } finally {
        errorMessage?.let {
          Log.e(TAG, "logout: $errorMessage")
        }
        Log.d(TAG, "logout: finally..")
        setValue(null)
      }
    }
  }

  fun setValue(newValue: AuthToken?) {
    GlobalScope.launch(Main) {
      if (_cacheToken.value != newValue) {
        _cacheToken.value = newValue
      }
    }
  }

  fun isConnectedToTheInternet(): Boolean {
    val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    try {
      return  cm.activeNetworkInfo!!.isConnected
    } catch (e: Exception) {
      Log.e(TAG, "isConnectedToTheInternet: ${e.message}" )
    }
    return false
  }
}