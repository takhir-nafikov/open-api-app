package com.takhir.openapiapp.ui.main.account

import androidx.lifecycle.LiveData
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.repository.main.AccountRepository
import com.takhir.openapiapp.session.SessionManager
import com.takhir.openapiapp.ui.BaseViewModel
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.main.account.state.*
import com.takhir.openapiapp.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
  val sessionManager: SessionManager,
  val accountRepository: AccountRepository
): BaseViewModel<AccountStateEvent, AccountViewState>() {

  override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
    when(stateEvent) {

      is GetAccountPropertiesEvent -> {
        return sessionManager.cachedToken.value?.let { authToken ->
          accountRepository.getAccountProperties(authToken)
        } ?: AbsentLiveData.create()
      }

      is UpdateAccountPropertiesEvent -> {
        return AbsentLiveData.create()
      }

      is ChangePasswordEvent -> {
        return AbsentLiveData.create()
      }

      is None -> {
        return AbsentLiveData.create()
      }
    }
  }

  override fun initNewViewState(): AccountViewState {
    return AccountViewState()
  }

  fun setAccountPropertiesData(accountProperties: AccountProperties) {
    val update = getCurrentViewStateOrNew()
    if (update.accountProperties == accountProperties) {
      return
    }
    update.accountProperties = accountProperties
    _viewState.value = update
  }

  fun logout() {
    sessionManager.logout()
  }

}