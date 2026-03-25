package com.groze.app.di

import android.content.Context
import androidx.room.Room
import com.groze.app.data.local.GrozeDatabase
import com.groze.app.data.local.dao.CartItemDao
import com.groze.app.data.local.dao.TripDao
import com.groze.app.data.local.dao.VaultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GrozeDatabase {
        return Room.databaseBuilder(
            context,
            GrozeDatabase::class.java,
            "groze_database"
        ).build()
    }

    @Provides
    fun provideVaultDao(database: GrozeDatabase): VaultDao = database.vaultDao()

    @Provides
    fun provideTripDao(database: GrozeDatabase): TripDao = database.tripDao()

    @Provides
    fun provideCartItemDao(database: GrozeDatabase): CartItemDao = database.cartItemDao()
}
