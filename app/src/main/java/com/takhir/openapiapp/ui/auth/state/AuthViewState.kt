package com.takhir.openapiapp.ui.auth.state

import com.takhir.openapiapp.models.AuthToken

data class AuthViewState(
  var registrationFields: RegistrationFields? = RegistrationFields(),
  var loginFields: LoginFields? = LoginFields(),
  var authToken: AuthToken? = null
)

data class RegistrationFields(
  val registrationEmail: String? = null,
  val registrationUsername: String? = null,
  val registrationPassword: String? = null,
  val registrationConfirmPassword: String? = null
) {

  class RegistrationError {
    companion object {

      fun mustFillAllFields(): String {
        return "All fields are required."
      }

      fun passwordDoNotMatch(): String {
        return "Password must match."
      }

      fun none(): String {
        return "None"
      }
    }
  }

  fun isValidForRegistration(): String {
    if (registrationEmail.isNullOrEmpty()
      || registrationUsername.isNullOrEmpty()
      || registrationPassword.isNullOrEmpty()
      || registrationConfirmPassword.isNullOrEmpty()) {
      return RegistrationError.mustFillAllFields()
    }

    if (registrationPassword != registrationConfirmPassword) {
      return RegistrationError.passwordDoNotMatch()
    }

    return RegistrationError.none()
  }
}

data class LoginFields(
  var loginEmail: String? = null,
  var loginPassword: String? = null
){
  class LoginError {

    companion object{

      fun mustFillAllFields(): String{
        return "You can't login without an email and password."
      }

      fun none():String{
        return "None"
      }

    }
  }
  fun isValidForLogin(): String {

    if(loginEmail.isNullOrEmpty()
      || loginPassword.isNullOrEmpty()){

      return LoginError.mustFillAllFields()
    }
    return LoginError.none()
  }

  override fun toString(): String {
    return "LoginState(email=$loginEmail, password=$loginPassword)"
  }
}
