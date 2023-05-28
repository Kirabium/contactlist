package com.virgile.listuser.di

import android.content.Context
import androidx.room.Room
import com.virgile.listuser.data.local.dao.ContactDao
import com.virgile.listuser.data.local.database.database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Provides
    fun provideContactDao(
        @ApplicationContext context: Context
    ): ContactDao {
        // Create and return an instance of ContactDao
        val db = Room.databaseBuilder(
            context,
            database::class.java,
            "database"
        ).build()
        return db.dao
    }
}
