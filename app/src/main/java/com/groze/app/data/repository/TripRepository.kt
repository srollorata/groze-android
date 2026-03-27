package com.groze.app.data.repository

import com.groze.app.data.local.dao.CartItemDao
import com.groze.app.data.local.dao.TripDao
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.CartItemStatus
import com.groze.app.data.local.entity.TripEntity
import com.groze.app.data.local.entity.TripStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val cartItemDao: CartItemDao
) {
    fun getAllTrips(): Flow<List<TripEntity>> = tripDao.getAllTrips()

    fun getActiveTripFlow(): Flow<TripEntity?> = tripDao.getTripByStatus(TripStatus.ACTIVE)

    fun getPlanningTripFlow(): Flow<TripEntity?> = tripDao.getTripByStatus(TripStatus.PLANNING)

    fun getActiveTrips(): Flow<List<TripEntity>> = tripDao.getActiveTrips()

    fun getCompletedTrips(): Flow<List<TripEntity>> = tripDao.getCompletedTrips()

    suspend fun getTripById(id: Long): TripEntity? = tripDao.getTripById(id)

    suspend fun createTrip(): Long {
        val trip = TripEntity(status = TripStatus.PLANNING)
        return tripDao.insertTrip(trip)
    }

    suspend fun startShopping(tripId: Long) {
        val trip = tripDao.getTripById(tripId) ?: return
        tripDao.updateTrip(
            trip.copy(
                status = TripStatus.ACTIVE,
                expectedTotal = trip.expectedTotal
            )
        )
    }

    suspend fun finishTrip(tripId: Long) {
        val trip = tripDao.getTripById(tripId) ?: return
        tripDao.updateTrip(
            trip.copy(
                status = TripStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateTripTotals(tripId: Long, expected: Double, actual: Double) {
        val trip = tripDao.getTripById(tripId) ?: return
        tripDao.updateTrip(trip.copy(expectedTotal = expected, actualTotal = actual))
    }

    suspend fun deleteTripById(id: Long) = tripDao.deleteTripById(id)

    // Cart item operations
    fun getCartItems(tripId: Long): Flow<List<CartItemEntity>> =
        cartItemDao.getItemsByTripId(tripId)

    fun getCartItemCount(tripId: Long): Flow<Int> = cartItemDao.getItemCount(tripId)

    fun getCheckedItemCount(tripId: Long): Flow<Int> = cartItemDao.getCheckedItemCount(tripId)

    suspend fun addCartItem(item: CartItemEntity): Long = cartItemDao.insertItem(item)

    suspend fun updateCartItem(item: CartItemEntity) = cartItemDao.updateItem(item)

    suspend fun deleteCartItem(itemId: Long) {
        val item = cartItemDao.getItemById(itemId) ?: return
        cartItemDao.deleteItem(item)
    }

    suspend fun checkItem(itemId: Long, actualPrice: Double? = null) {
        val item = cartItemDao.getItemById(itemId) ?: return
        cartItemDao.updateItem(
            item.copy(
                status = CartItemStatus.CHECKED,
                actualPrice = actualPrice ?: item.plannedPrice
            )
        )
    }

    suspend fun skipItem(itemId: Long) {
        val item = cartItemDao.getItemById(itemId) ?: return
        cartItemDao.updateItem(item.copy(status = CartItemStatus.SKIPPED, actualPrice = 0.0))
    }

    suspend fun uncheckItem(itemId: Long) {
        val item = cartItemDao.getItemById(itemId) ?: return
        cartItemDao.updateItem(item.copy(status = CartItemStatus.PENDING, actualPrice = null))
    }

    suspend fun updateItemPrice(itemId: Long, newPrice: Double) {
        val item = cartItemDao.getItemById(itemId) ?: return
        cartItemDao.updateItem(item.copy(actualPrice = newPrice))
    }

    suspend fun updateItemQuantity(itemId: Long, newQuantity: Int) {
        val item = cartItemDao.getItemById(itemId) ?: return
        cartItemDao.updateItem(item.copy(quantity = newQuantity))
    }

    suspend fun addAdHocItem(tripId: Long, name: String, price: Double, category: String = "", unit: String = "", quantity: Int = 1) {
        cartItemDao.insertItem(
            CartItemEntity(
                tripId = tripId,
                name = name,
                category = category,
                unit = unit,
                plannedPrice = 0.0,
                actualPrice = price,
                status = CartItemStatus.CHECKED,
                isAdHoc = true,
                iconName = "add_shopping_cart"
            )
        )
    }

    suspend fun clearCart(tripId: Long) = cartItemDao.deleteAllByTripId(tripId)
}
