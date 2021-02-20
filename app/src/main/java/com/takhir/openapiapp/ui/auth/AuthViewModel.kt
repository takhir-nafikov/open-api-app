package com.takhir.openapiapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.takhir.openapiapp.api.auth.network_responses.LoginResponse
import com.takhir.openapiapp.api.auth.network_responses.RegistrationResponse
import com.takhir.openapiapp.models.AuthToken
import com.takhir.openapiapp.repository.auth.AuthRepository
import com.takhir.openapiapp.ui.BaseViewModel
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.auth.state.AuthStateEvent
import com.takhir.openapiapp.ui.auth.state.AuthStateEvent.*
import com.takhir.openapiapp.ui.auth.state.AuthViewState
import com.takhir.openapiapp.ui.auth.state.LoginFields
import com.takhir.openapiapp.ui.auth.state.RegistrationFields
import com.takhir.openapiapp.util.AbsentLiveData
import com.takhir.openapiapp.util.GenericApiResponse
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
        return AbsentLiveData.create()
      }

      is RegisterAttemptEvent -> {
        return AbsentLiveData.create()
      }

      is CheckPreviousAuthEvent -> {
        return AbsentLiveData.create()
      }
    }
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

  override fun initNewViewState(): AuthViewState {
    return AuthViewState()
  }
}