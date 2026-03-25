package com.groze.app.ui.activetrip

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.CartItemStatus
import com.groze.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTripScreen(
    viewModel: ActiveTripViewModel,
    onClose: () -> Unit,
    onFinishTrip: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val priceSheetItem by viewModel.showPriceSheet.collectAsState()
    val showAddAdHoc by viewModel.showAddAdHocSheet.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Group items by category
    val groupedItems = uiState.items.groupBy { it.category.ifBlank { "Other" } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Active Trip",
                        fontWeight = FontWeight.ExtraBold,
                        color = GrozePrimary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GrozePrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = GrozePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrozeSurface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GrozeSurface.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add ad-hoc button
                IconButton(
                    onClick = { viewModel.showAddAdHoc() },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GrozeSurfaceContainerHigh)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add item", tint = GrozeOnSurface)
                }

                // Finish Trip button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val tripId = viewModel.finishTrip()
                            onFinishTrip(tripId)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GrozePrimary,
                        contentColor = GrozeOnPrimary
                    )
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finish Trip", fontWeight = FontWeight.ExtraBold)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Plan Overview card
            item {
                PlanOverviewCard(
                    expectedTotal = uiState.expectedTotal,
                    actualTotal = uiState.actualTotal,
                    checkedItems = uiState.checkedItems,
                    totalItems = uiState.totalItems
                )
            }

            // Grouped items by category
            groupedItems.forEach { (category, items) ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            category.uppercase(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = GrozeOnSurfaceVariant
                        )
                        Text(
                            "${items.size} items",
                            fontSize = 12.sp,
                            color = GrozeOutline
                        )
                    }
                }

                items(items, key = { it.id }) { item ->
                    ActiveItemCard(
                        item = item,
                        onCheck = { viewModel.checkItem(item) },
                        onSkip = { viewModel.skipItem(item) },
                        onUpdatePrice = { viewModel.showPriceUpdate(item) }
                    )
                }
            }

            // Bottom spacer
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Price update bottom sheet
    if (priceSheetItem != null) {
        UpdatePriceSheet(
            item = priceSheetItem!!,
            onDismiss = viewModel::dismissPriceSheet,
            onConfirm = { price -> viewModel.updatePrice(priceSheetItem!!.id, price) }
        )
    }

    // Add ad-hoc item sheet
    if (showAddAdHoc) {
        AddAdHocItemSheet(
            onDismiss = viewModel::dismissAddAdHoc,
            onAdd = { name, price, category, unit ->
                viewModel.addAdHocItem(name, price, category, unit)
            }
        )
    }
}

@Composable
fun PlanOverviewCard(
    expectedTotal: Double,
    actualTotal: Double,
    checkedItems: Int,
    totalItems: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GrozeSurfaceContainerLow)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    "TRIP PROGRESS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = GrozeOnSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Plan Overview",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = GrozeOnSurface
                )
            }
            Text(
                "$checkedItems of $totalItems items",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GrozePrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Expected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrozeSurfaceContainerLowest)
                    .padding(16.dp)
            ) {
                Text(
                    "EXPECTED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GrozeOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$${String.format("%.2f", expectedTotal)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrozeOnSurface
                )
            }

            // Actual
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrozePrimaryContainer)
                    .padding(16.dp)
            ) {
                Text(
                    "ACTUAL",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GrozeOnPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$${String.format("%.2f", actualTotal)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GrozeOnPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun ActiveItemCard(
    item: CartItemEntity,
    onCheck: () -> Unit,
    onSkip: () -> Unit,
    onUpdatePrice: () -> Unit
) {
    val isChecked = item.status == CartItemStatus.CHECKED
    val isSkipped = item.status == CartItemStatus.SKIPPED
    val priceChanged = item.actualPrice != null && item.actualPrice != item.plannedPrice && !item.isAdHoc
    val priceDelta = if (priceChanged) (item.actualPrice!! - item.plannedPrice) else 0.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isChecked -> GrozePrimaryContainer
                    isSkipped -> GrozeSurfaceContainerLowest.copy(alpha = 0.5f)
                    else -> GrozeSurfaceContainerLowest
                }
            )
            .then(
                if (item.isAdHoc) Modifier
                    .background(GrozeSurfaceContainerLowest)
                else Modifier
            )
            .alpha(if (isSkipped) 0.5f else 1f)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (item.isAdHoc) GrozeSecondaryContainer.copy(alpha = 0.3f)
                    else GrozeSurfaceContainerHigh
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.isAdHoc) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = null,
                    tint = GrozeSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Item details
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.name,
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) GrozeOnPrimaryContainer else GrozeOnSurface,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.alpha(if (isChecked) 0.6f else 1f)
                )
                if (item.isAdHoc) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "NEW",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrozeOnSecondaryFixed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(GrozeSecondaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    buildString {
                        if (item.quantity > 1) append("${item.quantity} × ")
                        if (item.unit.isNotBlank()) append("${item.unit} • ")
                        append("$${String.format("%.2f", item.actualPrice ?: item.plannedPrice)}")
                    },
                    fontSize = 11.sp,
                    color = GrozeOnSurfaceVariant
                )
                if (priceChanged) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${if (priceDelta > 0) "+" else ""}$${String.format("%.2f", priceDelta)}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrozeError,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(GrozeErrorContainer.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Action buttons
        if (!isSkipped) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!isChecked && !item.isAdHoc) {
                    // Price update button
                    IconButton(
                        onClick = onUpdatePrice,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Sell,
                            contentDescription = "Update price",
                            tint = GrozeOnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Check/uncheck button
                IconButton(
                    onClick = onCheck,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .then(
                            if (isChecked) Modifier.background(GrozePrimary)
                            else Modifier
                        )
                ) {
                    Icon(
                        if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (isChecked) "Uncheck" else "Check",
                        tint = if (isChecked) GrozeOnPrimary else GrozeOutlineVariant
                    )
                }
            }
        } else {
            Icon(
                Icons.Default.Block,
                contentDescription = "Skipped",
                tint = GrozeOnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
