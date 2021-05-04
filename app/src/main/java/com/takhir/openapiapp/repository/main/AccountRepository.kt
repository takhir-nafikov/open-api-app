package com.takhir.openapiapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.takhir.openapiapp.api.GenericResponse
import com.takhir.openapiapp.api.main.OpenApiMainService
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.repository.NetworkBoundResource
import com.takhir.openapiapp.session.SessionManager
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.Response
import com.takhir.openapiapp.ui.ResponseType
import com.takhir.openapiapp.ui.main.account.state.AccountViewState
import com.takhir.openapiapp.util.AbsentLiveData
import com.takhir.openapiapp.util.ApiSuccessResponse
import com.takhir.openapiapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
  val openApiMainService: OpenApiMainService,
  val accountPropertiesDao: AccountPropertiesDao,
  val sessionManager: SessionManager
) {
  private val TAG = "AppDebug"

  private var repositoryJob: Job? = null

  fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
    return object: NetworkBoundResource<AccountProperties, AccountProperties,  AccountViewState>(
      isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
      isNetworkRequest = true,
      shouldCancelIfNoInternet = false,
      shouldLoadFromCache = true
    ) {

      override fun loadFromCache(): LiveData<AccountViewState> {
        return accountPropertiesDao.searchByPk(authToken.account_pk!!)
          .switchMap {
            object: LiveData<AccountViewState>() {
              override fun onActive() {
                super.onActive()
                value = AccountViewState(it)
              }
            }
          }
      }

      override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
        cacheObject?.let {
          accountPropertiesDao.updateAccountProperties(
            cacheObject.pk,
            cacheObject.email,
            cacheObject.username
          )
        }
      }

      override suspend fun createCacheRequestAndReturn() {
        withContext(Main) {

          //finishing by viewing the db cache
          result.addSource(loadFromCache()) { viewState ->
            onCompleteJob(DataState.data(
              data = viewState,
              response = null
            ))
          }
        }
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
        updateLocalDb(response.body)

        withContext(Main) {

        createCacheRequestAndReturn()
        }
      }

      override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
        return openApiMainService
          .getAccountProperties(
            "Token ${authToken.token}"
          )
      }

      override fun setJob(job: Job) {
        repositoryJob?.cancel()
        repositoryJob = job
      }

    }.asLiveData()
  }

  fun saveAccountProperties(
    authToken: AuthToken,
    accountProperties: AccountProperties
  ): LiveData<DataState<AccountViewState>> {
    return object: NetworkBoundResource<GenericResponse, Any, AccountViewState>(
      isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
      isNetworkRequest = true,
      shouldCancelIfNoInternet = true,
      shouldLoadFromCache = false
    ) {

      // Not applicable
      override suspend fun createCacheRequestAndReturn() {
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
        updateLocalDb(null)

        withContext(Main) {

          //finish with success response
          onCompleteJob(
            DataState.data(
              data = null,
              response = Response(response.body.response, ResponseType.Toast)
            )
          )
        }
      }

      override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
        return openApiMainService.saveAccountProperties(
          "Token ${authToken.token}",
          accountProperties.email,
          accountProperties.username
        )
      }

      override fun loadFromCache(): LiveData<AccountViewState> {
        return AbsentLiveData.create()
      }

      override suspend fun updateLocalDb(cacheObject: Any?) {
        return accountPropertiesDao.updateAccountProperties(
          accountProperties.pk,
          accountProperties.email,
          accountProperties.username
        )
      }

      override fun setJob(job: Job) {
        repositoryJob?.cancel()
        repositoryJob = job
      }

    }.asLiveData()
  }

  fun cancelActiveJobs() {
    Log.d(TAG, "AccountRepository: cancelActiveJobs")
  }
}