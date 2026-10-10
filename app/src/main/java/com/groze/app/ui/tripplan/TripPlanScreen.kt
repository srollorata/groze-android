package com.groze.app.ui.tripplan

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.VaultItemEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlanScreen(
    viewModel: TripPlanViewModel,
    onBack: () -> Unit,
    onStartShopping: (Long) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val vaultResults by viewModel.vaultResults.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    val expectedTotal by viewModel.expectedTotal.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showAddNew by remember { mutableStateOf(false) }

    // Track which vault items are already in the cart
    val cartVaultIds = cartItems.mapNotNull { it.vaultItemId }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New Trip Plan",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            // Sticky bottom CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                if (cartItemCount > 0) {
                    // Show Start Shopping button when we have items
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Expected total
                        Text(
                            "Expected: ${viewModel.formatPrice(expectedTotal)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val tripId = viewModel.startShopping()
                                    onStartShopping(tripId)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Start Shopping", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$cartItemCount",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        enabled = false
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Add items to start", fontWeight = FontWeight.Bold)
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search bar
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("Search Vault...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Add new item option (when search has text)
            if (searchQuery.isNotBlank()) {
                item {
                    AddNewItemCard(
                        searchQuery = searchQuery,
                        onAdd = { showAddNew = true }
                    )
                }
            }

            // Vault matches header
            if (vaultResults.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "VAULT MATCHES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${vaultResults.size} ITEMS FOUND",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Vault items list
            items(vaultResults, key = { it.id }) { vaultItem ->
                val alreadyInCart = vaultItem.id in cartVaultIds
                VaultMatchCard(
                    item = vaultItem,
                    alreadyInCart = alreadyInCart,
                    onAdd = { viewModel.addVaultItemToCart(vaultItem) }
                )
            }

            // Cart items section (only show when cart is not empty)
            if (cartItems.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "YOUR CART",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${cartItemCount} items",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        currency = currency,
                        formatPrice = viewModel::formatPrice,
                        onQuantityChange = { newQuantity ->
                            viewModel.updateItemQuantity(item, newQuantity)
                        },
                        onRemove = { viewModel.removeCartItem(item) }
                    )
                }
            }

            // Frequent Pairs section
            item {
                Column {
                    Text(
                        "FREQUENT PAIRS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val suggestions = listOf("Granola", "Honey", "Blueberries", "Chia Seeds")
                        items(suggestions) { suggestion ->
                            Text(
                                suggestion,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // Bottom spacer
            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
    }

    // Add new item sheet
    if (showAddNew) {
        com.groze.app.ui.vault.AddEditVaultItemSheet(
            item = com.groze.app.data.local.entity.VaultItemEntity(
                name = searchQuery,
                category = "",
                lastPrice = 0.0
            ),
            isAdd = true,
            currency = currency,
            onDismiss = { showAddNew = false },
            onSave = { name, category, price, unit ->
                viewModel.addNewItemToVaultAndCart(name, category, price, unit)
                showAddNew = false
            }
        )
    }
}

@Composable
fun AddNewItemCard(searchQuery: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .clickable(onClick = onAdd)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "VAULT REGISTRY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                buildString {
                    append("Add ")
                    append("\"$searchQuery\"")
                    append(" as new item")
                },
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun VaultMatchCard(
    item: VaultItemEntity,
    alreadyInCart: Boolean,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Item icon placeholder
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${item.unit} • ${item.category}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onAdd,
            enabled = !alreadyInCart,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (alreadyInCart) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add to cart",
                tint = if (alreadyInCart) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemEntity,
    currency: String,
    formatPrice: (Double) -> String,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val itemTotal = item.plannedPrice * item.quantity

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Item icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Item details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                if (item.unit.isNotBlank()) "${item.unit} • ${formatPrice(item.plannedPrice)}/unit" else "${formatPrice(item.plannedPrice)}/unit",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Total: ${formatPrice(itemTotal)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Quantity controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { onQuantityChange(item.quantity - 1) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Decrease quantity",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                "${item.quantity}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            IconButton(
                onClick = { onQuantityChange(item.quantity + 1) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase quantity",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
