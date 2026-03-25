package com.groze.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.groze.app.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Query("SELECT * FROM cart_items WHERE tripId = :tripId ORDER BY category ASC, name ASC")
    fun getItemsByTripId(tripId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getItemById(id: Long): CartItemEntity?

    @Query("SELECT COUNT(*) FROM cart_items WHERE tripId = :tripId")
    fun getItemCount(tripId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM cart_items WHERE tripId = :tripId AND status = 'CHECKED'")
    fun getCheckedItemCount(tripId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CartItemEntity>)

    @Update
    suspend fun updateItem(item: CartItemEntity)

    @Delete
    suspend fun deleteItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE tripId = :tripId")
    suspend fun deleteAllByTripId(tripId: Long)
}
