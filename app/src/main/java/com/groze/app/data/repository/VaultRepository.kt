package com.groze.app.data.repository

import com.groze.app.data.local.dao.VaultDao
import com.groze.app.data.local.entity.VaultItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepository @Inject constructor(
    private val vaultDao: VaultDao
) {
    fun getAllItems(): Flow<List<VaultItemEntity>> = vaultDao.getAllItems()

    fun searchItems(query: String): Flow<List<VaultItemEntity>> = vaultDao.searchItems(query)

    suspend fun getItemById(id: Long): VaultItemEntity? = vaultDao.getItemById(id)

    suspend fun insertItem(item: VaultItemEntity): Long = vaultDao.insertItem(item)

    suspend fun updateItem(item: VaultItemEntity) = vaultDao.updateItem(item)

    suspend fun deleteItem(item: VaultItemEntity) = vaultDao.deleteItem(item)

    suspend fun deleteItemById(id: Long) = vaultDao.deleteItemById(id)

    suspend fun updatePrice(id: Long, newPrice: Double) {
        val item = vaultDao.getItemById(id) ?: return
        vaultDao.updateItem(
            item.copy(
                lastPrice = newPrice,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
