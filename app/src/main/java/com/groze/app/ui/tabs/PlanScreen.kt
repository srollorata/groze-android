package com.groze.app.ui.tabs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groze.app.ui.theme.GrozeBackground
import com.groze.app.ui.theme.GrozeOnPrimary
import com.groze.app.ui.theme.GrozeOnSurface
import com.groze.app.ui.theme.GrozeOnSurfaceVariant
import com.groze.app.ui.theme.GrozePrimary
import com.groze.app.ui.theme.GrozePrimaryContainer
import com.groze.app.ui.theme.GrozePrimaryDim
import com.groze.app.ui.theme.GrozeSecondaryContainer
import com.groze.app.ui.theme.GrozeSurface
import com.groze.app.ui.theme.GrozeSurfaceContainerLow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: PlanViewModel = hiltViewModel(),
    onCreateNewCart: (Long) -> Unit
) {
    val planningTripId by viewModel.planningTrip.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Plan",
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = GrozePrimary.copy(alpha = 0.15f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GrozePrimary, GrozePrimaryDim)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GrozePrimaryContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EditCalendar,
                                contentDescription = null,
                                tint = GrozeOnPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Plan Your Next Trip",
                                style = MaterialTheme.typography.titleLarge,
                                color = GrozeOnPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Curate your shopping list",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrozeOnPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(value = "0", label = "Items")
                        StatItem(value = "$0.00", label = "Estimated")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = GrozeOnSurface.copy(alpha = 0.05f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(GrozeSurfaceContainerLow)
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = GrozePrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Start Planning",
                        style = MaterialTheme.typography.titleMedium,
                        color = GrozeOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Select items from your vault to create a new shopping trip",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrozeOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val tripId = viewModel.createNewCart()
                                onCreateNewCart(tripId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrozePrimary,
                            contentColor = GrozeOnPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tips Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GrozeSecondaryContainer.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "Tips",
                        style = MaterialTheme.typography.titleSmall,
                        color = GrozeOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• Add frequently bought items to your Vault\n" +
                                "• Use categories to organize items\n" +
                                "• Check expected prices before shopping",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrozeOnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            color = GrozeOnPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = GrozeOnPrimary.copy(alpha = 0.7f)
        )
    }
}
