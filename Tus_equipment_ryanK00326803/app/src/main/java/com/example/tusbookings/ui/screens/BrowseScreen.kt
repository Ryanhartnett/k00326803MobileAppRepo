package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusbookings.ui.BookingsUiState
import com.example.tusbookings.ui.components.ItemCard

@Composable
fun BrowseScreen(
    state: BookingsUiState,
    onItemOpen: (Int) -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }
    var selectedTypeId by rememberSaveable { mutableStateOf<Int?>(null) }
    var availableOnly by rememberSaveable { mutableStateOf(false) }
    var typeMenuOpen by remember { mutableStateOf(false) }
    val typeLabel = state.types.firstOrNull { it.id == selectedTypeId }?.name ?: "All types"

    val visibleItems = state.items.filter { item ->
        (selectedTypeId == null || item.typeId == selectedTypeId) &&
                (!availableOnly || item.unitsAvailable > 0) &&
                (search.isBlank() || item.name.contains(search.trim(), ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Text("Browse equipment", style = MaterialTheme.typography.headlineMedium) }
        item {
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                label = { Text("Search by name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    OutlinedButton(onClick = { typeMenuOpen = true }) { Text(typeLabel) }
                    DropdownMenu(expanded = typeMenuOpen,
                        onDismissRequest = { typeMenuOpen = false }) {
                        DropdownMenuItem(text = { Text("All types") }, onClick = {
                            selectedTypeId = null
                            typeMenuOpen = false
                        })
                        state.types.forEach { type ->
                            DropdownMenuItem(text = { Text(type.name) }, onClick = {
                                selectedTypeId = type.id
                                typeMenuOpen = false
                            })
                        }
                    }
                }
                FilterChip(
                    selected = availableOnly,
                    onClick = { availableOnly = !availableOnly },
                    label = { Text("Available now") }
                )
            }
        }
        if (visibleItems.isEmpty()) { item { Text("No equipment matches your filters.") } }
        items(visibleItems, key = { it.id }) { item ->
            ItemCard(item = item, typeName = state.typeName(item.typeId),
                onOpen = { onItemOpen(item.id) })
        }
    }
}
