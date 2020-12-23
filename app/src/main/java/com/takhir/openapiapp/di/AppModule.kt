package com.takhir.openapiapp.di

import android.app.Application
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.takhir.openapiapp.R
import com.takhir.openapiapp.persistence.AccountPropertiesDao
import com.takhir.openapiapp.persistence.AppDatabase
import com.takhir.openapiapp.persistence.AppDatabase.Companion.DATABASE_NAME
import com.takhir.openapiapp.persistence.AuthTokenDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppDb(app: Application) : AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthTokenDao(db: AppDatabase): AuthTokenDao {
        return db.getAuthTokenDao()
    }

    @Singleton
    @Provides
    fun provideAccountPropertiesDao(db: AppDatabase): AccountPropertiesDao {
        return db.getAccountPropertiesDao()
    }

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        // todo выбрать нормальные дефолтные рисунки
        return RequestOptions
            .placeholderOf(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(application: Application, requestOptions: RequestOptions) : RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }
}