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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.CartItemStatus
import kotlinx.coroutines.launch

private val CheckedItemBackground = Color(0xFFE8F5E9)

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
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
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
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add item", tint = MaterialTheme.colorScheme.onSurface)
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
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finish Trip", fontWeight = FontWeight.ExtraBold)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${items.size} items",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                items(items, key = { it.id }) { item ->
                    ActiveItemCard(
                        item = item,
                        onCheck = { viewModel.checkItem(item) },
                        onSkip = { viewModel.skipItem(item) },
                        onUpdatePrice = { viewModel.showPriceUpdate(item) },
                        onQuantityChange = { newQuantity -> viewModel.updateItemQuantity(item, newQuantity) }
                    )
                }
            }
        }
    }

    // Price update bottom sheet
    priceSheetItem?.let { item ->
        UpdatePriceSheet(
            item = item,
            onDismiss = viewModel::dismissPriceSheet,
            onConfirm = { price -> viewModel.updatePrice(item.id, price) }
        )
    }

    // Add ad-hoc item sheet
    if (showAddAdHoc) {
        AddAdHocItemSheet(
            onDismiss = viewModel::dismissAddAdHoc,
            onAdd = { name, price, category, unit, quantity ->
                viewModel.addAdHocItem(name, price, category, unit, quantity)
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
    val isUnderBudget = actualTotal <= expectedTotal

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Plan Overview",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                "$checkedItems of $totalItems items",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                tonalElevation = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "EXPECTED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$${String.format("%.2f", expectedTotal)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                tonalElevation = if (isUnderBudget) 4.dp else 1.dp,
                color = if (isUnderBudget) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ACTUAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isUnderBudget) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$${String.format("%.2f", actualTotal)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isUnderBudget) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveItemCard(
    item: CartItemEntity,
    onCheck: () -> Unit,
    onSkip: () -> Unit,
    onUpdatePrice: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    val isChecked = item.status == CartItemStatus.CHECKED
    val isSkipped = item.status == CartItemStatus.SKIPPED
    val priceChanged = item.actualPrice != null && item.actualPrice != item.plannedPrice && !item.isAdHoc
    val priceDelta = if (priceChanged) (item.actualPrice!! - item.plannedPrice) else 0.0
    val itemTotal = (item.actualPrice ?: item.plannedPrice) * item.quantity

    val checkedBackgroundColor = CheckedItemBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isChecked -> checkedBackgroundColor
                    isSkipped -> MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.surfaceContainerLowest
                }
            )
            .then(
                if (item.isAdHoc) Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                else Modifier
            )
            .alpha(if (isChecked) 0.7f else if (isSkipped) 0.5f else 1f)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (item.isAdHoc) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.isAdHoc) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                        color = if (isChecked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.alpha(if (isChecked) 0.6f else 1f)
                    )
                    if (item.isAdHoc) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "NEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val hasQuantity = item.quantity > 1
                    val hasUnit = item.unit.isNotBlank()
                    val metadataText = buildString {
                        if (hasQuantity) append("${item.quantity} × ")
                        if (hasUnit) append("${item.unit}")
                        if (hasQuantity || hasUnit) append(" • ")
                        append("$${String.format("%.2f", item.actualPrice ?: item.plannedPrice)}")
                    }
                    Text(
                        metadataText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (priceChanged) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${if (priceDelta > 0) "+" else ""}$${String.format("%.2f", priceDelta)}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (item.quantity > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Total: $${String.format("%.2f", itemTotal)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        if (!isSkipped) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Quantity controls (only for unchecked items)
                if (!isChecked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onQuantityChange(item.quantity - 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease quantity",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            "${item.quantity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { onQuantityChange(item.quantity + 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase quantity",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (!isChecked && !item.isAdHoc) {
                    // Price update button
                    IconButton(
                        onClick = onUpdatePrice,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Sell,
                            contentDescription = "Update price",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            if (isChecked) Modifier.background(MaterialTheme.colorScheme.primary)
                            else Modifier
                        )
                ) {
                    Icon(
                        if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (isChecked) "Uncheck" else "Check",
                        tint = if (isChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        } else {
            Icon(
                Icons.Default.Block,
                contentDescription = "Skipped",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
