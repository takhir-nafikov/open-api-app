package com.takhir.openapiapp.ui.main.account.state

import com.takhir.openapiapp.models.AccountProperties

sealed class AccountStateEvent

data class UpdateAccountPropertiesEvent(
  val email: String,
  val username: String
): AccountStateEvent()

data class ChangePasswordEvent(
  val currentPassword: String,
  val newPassword: String,
  val confirmPassword: String
): AccountStateEvent()

object GetAccountPropertiesEvent: AccountStateEvent()

object None: AccountStateEvent()
