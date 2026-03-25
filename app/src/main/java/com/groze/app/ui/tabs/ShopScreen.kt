package com.groze.app.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groze.app.data.local.entity.TripEntity
import com.groze.app.ui.theme.GrozeBackground
import com.groze.app.ui.theme.GrozeError
import com.groze.app.ui.theme.GrozeOnError
import com.groze.app.ui.theme.GrozeOnSurface
import com.groze.app.ui.theme.GrozeOnSurfaceVariant
import com.groze.app.ui.theme.GrozePrimary
import com.groze.app.ui.theme.GrozePrimaryContainer
import com.groze.app.ui.theme.GrozeSurface
import com.groze.app.ui.theme.GrozeSurfaceContainerLow
import com.groze.app.ui.theme.GrozeSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    onOpenTrip: (Long) -> Unit
) {
    val activeTrips by viewModel.activeTrips.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Shop",
                        fontWeight = FontWeight.ExtraBold,
                        color = GrozePrimary,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrozeBackground
                )
            )
        },
        containerColor = GrozeBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (activeTrips.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(GrozeSurfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingCartCheckout,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = GrozeSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Active Trips",
                            style = MaterialTheme.typography.titleMedium,
                            color = GrozeOnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Start a shopping trip from the Plan tab",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrozeOnSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    "Active Shopping",
                    style = MaterialTheme.typography.titleMedium,
                    color = GrozeOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeTrips, key = { it.id }) { trip ->
                        ActiveTripCard(
                            trip = trip,
                            onClick = { onOpenTrip(trip.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ActiveTripCard(
    trip: TripEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = GrozePrimary.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(GrozePrimaryContainer)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrozePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = GrozeSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Shopping Trip #${trip.id}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GrozeOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "In Progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrozePrimary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$${String.format("%.2f", trip.actualTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GrozeOnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "of $${String.format("%.2f", trip.expectedTotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrozeOnSurfaceVariant
                )
            }
        }
    }
}
