package com.takhir.openapiapp.ui.auth

import androidx.lifecycle.LiveData
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.repository.auth.AuthRepository
import com.takhir.openapiapp.ui.BaseViewModel
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.auth.state.*
import com.takhir.openapiapp.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
  val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>()
{

  override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
    when (stateEvent) {
      is LoginAttemptEvent -> {
        return authRepository.attemptLogin(
          stateEvent.email,
          stateEvent.password
        )
      }

      is RegisterAttemptEvent -> {
        return authRepository.attemptRegistration(
          stateEvent.email,
          stateEvent.username,
          stateEvent.password,
          stateEvent.confirmPassword
        )
      }

      is CheckPreviousAuthEvent -> {
        return authRepository.checkPreviousAuthUser()
      }

      is None -> {
        return object: LiveData<DataState<AuthViewState>>() {
          override fun onActive() {
            super.onActive()
            value = DataState.data(data = null, response = null)
          }
        }
      }
    }
  }

  override fun initNewViewState(): AuthViewState {
    return AuthViewState()
  }

  fun setRegistrationField(registrationFields: RegistrationFields) {
    val update = getCurrentViewStateOrNew()
    if (update.registrationFields == registrationFields) return
    update.registrationFields = registrationFields
    _viewState.value = update
  }

  fun setLoginFields(loginFields: LoginFields) {
    val update = getCurrentViewStateOrNew()
    if (update.loginFields == loginFields) return
    update.loginFields = loginFields
    _viewState.value = update
  }

  fun setAuthToken(authToken: AuthToken) {
    val update = getCurrentViewStateOrNew()
    if (update.authToken == authToken) return
    update.authToken = authToken
    _viewState.value = update
  }

  fun cancelActiveJobs(){
    handlePendingData()
    authRepository.cancelActiveJobs()
  }

  fun handlePendingData() {
    setStateEvent(None)
  }

  override fun onCleared() {
    super.onCleared()
    cancelActiveJobs()
  }

}