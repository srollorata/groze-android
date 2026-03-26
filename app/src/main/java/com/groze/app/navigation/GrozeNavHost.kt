package com.groze.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.groze.app.ui.activetrip.ActiveTripScreen
import com.groze.app.ui.activetrip.ActiveTripViewModel
import com.groze.app.ui.onboarding.OnboardingScreen
import com.groze.app.ui.tripplan.TripPlanScreen
import com.groze.app.ui.tripplan.TripPlanViewModel
import com.groze.app.ui.tripsummary.TripSummaryScreen
import com.groze.app.ui.tripsummary.TripSummaryViewModel
import com.groze.app.ui.tabs.PlanScreen
import com.groze.app.ui.tabs.ShopScreen
import com.groze.app.ui.tabs.HistoryScreen
import com.groze.app.ui.settings.SettingsScreen
import com.groze.app.ui.vault.VaultScreen
import com.groze.app.ui.vault.VaultViewModel

@Composable
fun GrozeNavHost(startOnboarding: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (startOnboarding) Screen.Onboarding.route else Screen.Vault.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute in listOf(
        Screen.Vault.route,
        Screen.Plan.route,
        Screen.Shop.route,
        Screen.History.route
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Vault.route) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
                }
            ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onFinish = {
                            navController.navigate(Screen.Vault.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Vault.route) {
                    val viewModel: VaultViewModel = hiltViewModel()
                    VaultScreen(
                        viewModel = viewModel,
                        onCreateNewCart = { tripId ->
                            navController.navigate(Screen.NewTripPlan.createRoute(tripId))
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }

                composable(Screen.Plan.route) {
                    PlanScreen(
                        onCreateNewCart = { tripId ->
                            navController.navigate(Screen.NewTripPlan.createRoute(tripId))
                        }
                    )
                }

                composable(Screen.Shop.route) {
                    ShopScreen(
                        onOpenTrip = { tripId ->
                            navController.navigate(Screen.ActiveTrip.createRoute(tripId))
                        }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen()
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.NewTripPlan.route,
                    arguments = listOf(navArgument("tripId") { type = NavType.LongType })
                ) {
                    val viewModel: TripPlanViewModel = hiltViewModel()
                    TripPlanScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onStartShopping = { tripId ->
                            navController.navigate(Screen.ActiveTrip.createRoute(tripId)) {
                                popUpTo(Screen.Vault.route)
                            }
                        }
                    )
                }

                composable(
                    route = Screen.ActiveTrip.route,
                    arguments = listOf(navArgument("tripId") { type = NavType.LongType })
                ) {
                    val viewModel: ActiveTripViewModel = hiltViewModel()
                    ActiveTripScreen(
                        viewModel = viewModel,
                        onClose = {
                            navController.popBackStack(Screen.Vault.route, false)
                        },
                        onFinishTrip = { tripId ->
                            navController.navigate(Screen.TripSummary.createRoute(tripId)) {
                                popUpTo(Screen.Vault.route)
                            }
                        }
                    )
                }

                composable(
                    route = Screen.TripSummary.route,
                    arguments = listOf(navArgument("tripId") { type = NavType.LongType })
                ) {
                    val viewModel: TripSummaryViewModel = hiltViewModel()
                    TripSummaryScreen(
                        viewModel = viewModel,
                        onDismiss = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(Screen.Vault.route)
                            }
                        }
                    )
                }
            }
        }
    }
}
