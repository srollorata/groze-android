package com.groze.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val vaultItemId: Long? = null,
    val name: String,
    val category: String = "",
    val quantity: Int = 1,
    val unit: String = "",
    val plannedPrice: Double = 0.0,
    val actualPrice: Double? = null,
    val status: String = CartItemStatus.PENDING,
    val isAdHoc: Boolean = false,
    val iconName: String = "inventory_2"
)

object CartItemStatus {
    const val PENDING = "PENDING"
    const val CHECKED = "CHECKED"
    const val SKIPPED = "SKIPPED"
}
