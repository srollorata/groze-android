package com.groze.app.ui.tripplan

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.groze.app.data.local.entity.VaultItemEntity
import com.groze.app.ui.theme.*
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
                        color = GrozePrimary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GrozePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrozeSurface.copy(alpha = 0.8f)
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
                                GrozeBackground.copy(alpha = 0f),
                                GrozeBackground,
                                GrozeBackground
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
                            "Expected: $${String.format("%.2f", expectedTotal)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrozeOnSurfaceVariant,
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
                                containerColor = GrozePrimary,
                                contentColor = GrozeOnPrimary
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Start Shopping", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(GrozeOnPrimary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$cartItemCount",
                                    color = GrozePrimary,
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
                            containerColor = GrozeSurfaceContainerHigh,
                            contentColor = GrozeOnSurfaceVariant
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
        containerColor = GrozeBackground
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
                    placeholder = { Text("Search Vault...", color = GrozeOnSurfaceVariant.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = GrozeOnSurfaceVariant)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GrozeSurfaceContainerHigh,
                        unfocusedContainerColor = GrozeSurfaceContainerHigh,
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
                            color = GrozeOnSurfaceVariant
                        )
                        Text(
                            "${vaultResults.size} ITEMS FOUND",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrozeOnSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(GrozeSurfaceContainerHighest)
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

            // Frequent Pairs section
            item {
                Column {
                    Text(
                        "FREQUENT PAIRS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = GrozeOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val suggestions = listOf("Granola", "Honey", "Blueberries", "Chia Seeds")
                        items(suggestions) { suggestion ->
                            Text(
                                suggestion,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(GrozeSecondaryContainer)
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrozeOnSecondaryFixed
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
            .background(GrozePrimaryContainer.copy(alpha = 0.4f))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(GrozePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = GrozeOnPrimary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "VAULT REGISTRY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = GrozeOnPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                buildString {
                    append("Add ")
                    append("\"$searchQuery\"")
                    append(" as new item")
                },
                fontWeight = FontWeight.Bold,
                color = GrozeOnPrimaryContainer
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
            .background(GrozeSurfaceContainerLowest)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Item icon placeholder
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GrozeSurfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = GrozePrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                fontWeight = FontWeight.Bold,
                color = GrozeOnSurface
            )
            Text(
                "${item.unit} • ${item.category}",
                fontSize = 12.sp,
                color = GrozeOnSurfaceVariant
            )
        }

        IconButton(
            onClick = onAdd,
            enabled = !alreadyInCart,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (alreadyInCart) GrozePrimaryContainer
                    else GrozeSurfaceContainerHigh
                )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add to cart",
                tint = if (alreadyInCart) GrozePrimary.copy(alpha = 0.5f) else GrozePrimary
            )
        }
    }
}
