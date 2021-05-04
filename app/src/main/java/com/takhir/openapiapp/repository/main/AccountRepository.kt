package com.takhir.openapiapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.takhir.openapiapp.api.main.OpenApiMainService
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.repository.NetworkBoundResource
import com.takhir.openapiapp.session.SessionManager
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.main.account.state.AccountViewState
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
      sessionManager.isConnectedToTheInternet(),
      true,
      true
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

  fun cancelActiveJobs() {
    Log.d(TAG, "AccountRepository: cancelActiveJobs")
  }
}