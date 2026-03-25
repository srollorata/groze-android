package com.groze.app.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Vault : Screen("vault")
    data object Plan : Screen("plan")
    data object Shop : Screen("shop")
    data object History : Screen("history")
    data object NewTripPlan : Screen("new_trip_plan/{tripId}") {
        fun createRoute(tripId: Long) = "new_trip_plan/$tripId"
    }
    data object ActiveTrip : Screen("active_trip/{tripId}") {
        fun createRoute(tripId: Long) = "active_trip/$tripId"
    }
    data object TripSummary : Screen("trip_summary/{tripId}") {
        fun createRoute(tripId: Long) = "trip_summary/$tripId"
    }
}
