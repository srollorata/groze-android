package com.groze.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.groze.app.data.local.dao.CartItemDao
import com.groze.app.data.local.dao.TripDao
import com.groze.app.data.local.dao.VaultDao
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.TripEntity
import com.groze.app.data.local.entity.VaultItemEntity

@Database(
    entities = [
        VaultItemEntity::class,
        TripEntity::class,
        CartItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class GrozeDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao
    abstract fun tripDao(): TripDao
    abstract fun cartItemDao(): CartItemDao
}
