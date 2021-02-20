package com.takhir.openapiapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.takhir.openapiapp.models.AccountProperties
import com.takhir.openapiapp.models.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

  companion object {

    const val DATABASE_NAME = "app_db"
  }

  abstract fun getAuthTokenDao(): AuthTokenDao

  abstract fun getAccountPropertiesDao(): AccountPropertiesDao

}