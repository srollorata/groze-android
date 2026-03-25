package com.groze.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val status: String = TripStatus.PLANNING,
    val expectedTotal: Double = 0.0,
    val actualTotal: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

object TripStatus {
    const val PLANNING = "PLANNING"
    const val ACTIVE = "ACTIVE"
    const val COMPLETED = "COMPLETED"
}
