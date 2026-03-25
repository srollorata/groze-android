package com.groze.app.ui.tripsummary

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groze.app.ui.theme.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSummaryScreen(
    viewModel: TripSummaryViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Trip Summary",
                        fontWeight = FontWeight.ExtraBold,
                        color = GrozePrimary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GrozePrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = GrozePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrozeSurface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GrozeSurface.copy(alpha = 0.9f))
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp, top = 12.dp)
            ) {
                Button(
                    onClick = { viewModel.confirmUpdates(onDismiss) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GrozePrimary,
                        contentColor = GrozeOnPrimary
                    )
                ) {
                    Text("Confirm Updates", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { viewModel.dismissUpdates(onDismiss) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Dismiss Updates",
                        fontWeight = FontWeight.SemiBold,
                        color = GrozeOnSurfaceVariant
                    )
                }
            }
        },
        containerColor = GrozeBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Original Plan
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(GrozeSurfaceContainerLow)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Original Plan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrozeOnSurfaceVariant
                        )
                        Text(
                            "$${String.format("%.2f", uiState.originalPlan)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GrozeOnSurface
                        )
                    }

                    // Actual Spend
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(GrozePrimaryContainer)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Actual Spend",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrozeOnPrimaryContainer
                        )
                        Column {
                            Text(
                                "$${String.format("%.2f", uiState.actualSpend)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GrozeOnPrimaryContainer
                            )
                            if (uiState.percentChange != 0.0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = GrozeOnPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${String.format("%.1f", abs(uiState.percentChange))}% ${if (uiState.percentChange > 0) "increase" else "decrease"}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GrozeOnPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Deltas header
            if (uiState.deltas.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Price Updates",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GrozeOnSurface
                        )
                        Text(
                            "${uiState.deltas.size} CHANGES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = GrozeOnSecondaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(GrozeSecondaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Delta items
            items(uiState.deltas) { delta ->
                DeltaCard(delta)
            }

            // Sync indicator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GrozeSecondaryContainer.copy(alpha = 0.4f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        tint = GrozePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Changes are ready to sync with your Vault.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrozeOnSecondaryFixedVariant
                    )
                }
            }

            // Bottom spacer
            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
    }
}

@Composable
fun DeltaCard(delta: TripDelta) {
    val isSkipped = delta.type == DeltaType.SKIPPED
    val isNew = delta.type == DeltaType.NEW_ITEM

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GrozeSurfaceContainerLowest)
            .alpha(if (isSkipped) 0.6f else 1f)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        isNew -> GrozeSecondaryContainer.copy(alpha = 0.5f)
                        isSkipped -> GrozeSurfaceVariant
                        else -> GrozeSurfaceContainerLow
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when {
                    isNew -> Icons.Default.AddCircle
                    isSkipped -> Icons.Default.Block
                    else -> Icons.Default.LocalDrink
                },
                contentDescription = null,
                tint = if (isSkipped) GrozeOnSurfaceVariant else GrozePrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Item info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    delta.item.name,
                    fontWeight = FontWeight.Bold,
                    color = GrozeOnSurface
                )
                if (isNew) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "NEW",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrozeOnTertiaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(GrozeTertiaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                when {
                    isSkipped -> "Out of stock"
                    isNew -> "${delta.item.unit} • ${delta.item.category}"
                    else -> "${delta.item.unit} • ${delta.item.category}"
                },
                fontSize = 12.sp,
                color = GrozeOnSurfaceVariant
            )
        }

        // Price info
        Column(horizontalAlignment = Alignment.End) {
            if (delta.type == DeltaType.PRICE_CHANGE) {
                Text(
                    "$${String.format("%.2f", delta.item.plannedPrice)}",
                    fontSize = 12.sp,
                    color = GrozeOnSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough
                )
                Text(
                    "$${String.format("%.2f", delta.item.actualPrice ?: delta.item.plannedPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrozeError
                )
            } else {
                Text(
                    "${if (delta.priceDifference >= 0) "+" else ""}$${String.format("%.2f", delta.priceDifference)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isSkipped -> GrozeOnSurfaceVariant
                        else -> GrozePrimary
                    }
                )
            }
        }
    }
}
