package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tusbookings.model.Item
import com.example.tusbookings.model.ItemType
import com.example.tusbookings.ui.BookingsUiState
import com.example.tusbookings.ui.components.ItemCard

@Composable
fun ManageItemsScreen(
    state: BookingsUiState,
    onAddItem: (Item) -> Unit,
    onUpdateItem: (Item) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onItemOpen: (Int) -> Unit
) {
    var showAddForm by rememberSaveable { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Item?>(null) }
    var itemToEdit by remember { mutableStateOf<Item?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Text("Manage equipment", style = MaterialTheme.typography.headlineMedium) }
        item {
            Button(onClick = { showAddForm = true },
                enabled = !state.isSaving && state.types.isNotEmpty()) {
                Text("Add new item")
            }
        }
        if (state.isSaving) { item { Text("Saving changes…") } }
        state.message?.let { message -> item { Text(message) } }
        items(state.items, key = { it.id }) { item ->
            Column {
                ItemCard(item = item, typeName = state.typeName(item.typeId),
                    onOpen = { onItemOpen(item.id) })
                Row {
                    TextButton(enabled = !state.isSaving,
                        onClick = { itemToEdit = item }) { Text("Edit") }
                    TextButton(enabled = !state.isSaving,
                        onClick = { itemToDelete = item }) { Text("Delete") }
                }
            }
        }
    }

    if (showAddForm) {
        AddItemDialog(
            types = state.types,
            onDismiss = { showAddForm = false },
            onSave = { item ->
                onAddItem(item)
                showAddForm = false
            }
        )
    }

    itemToEdit?.let { item ->
        AddItemDialog(
            types = state.types,
            existing = item,
            onDismiss = { itemToEdit = null },
            onSave = { updated ->
                onUpdateItem(updated)
                itemToEdit = null
            }
        )
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete item?") },
            text = { Text("Remove ${item.name} from TusBookings?") },
            confirmButton = {
                TextButton(enabled = !state.isSaving, onClick = {
                    onDeleteItem(item.id)
                    itemToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AddItemDialog(
    types: List<ItemType>,
    onDismiss: () -> Unit,
    onSave: (Item) -> Unit,
    existing: Item? = null
) {
    // When editing, the form starts filled in with the item's current details.
    val key = existing?.id
    var name by rememberSaveable(key) { mutableStateOf(existing?.name ?: "") }
    var description by rememberSaveable(key) { mutableStateOf(existing?.description ?: "") }
    var location by rememberSaveable(key) { mutableStateOf(existing?.location ?: "") }
    var units by rememberSaveable(key) { mutableStateOf((existing?.unitsTotal ?: 1).toString()) }
    var typeId by rememberSaveable(key) { mutableStateOf(existing?.typeId ?: types.firstOrNull()?.id) }
    var condition by rememberSaveable(key) { mutableStateOf(existing?.condition ?: "Good") }
    val onLoan = if (existing == null) 0 else existing.unitsTotal - existing.unitsAvailable
    val minUnits = maxOf(1, onLoan)
    var typeMenuOpen by remember { mutableStateOf(false) }
    var conditionMenuOpen by remember { mutableStateOf(false) }
    val conditions = listOf("New", "Very good", "Good", "Fair")
    val selectedType = types.firstOrNull { it.id == typeId }
    val unitsValue = units.toIntOrNull()
    val canSave = name.isNotBlank() && description.isNotBlank() &&
            location.isNotBlank() && selectedType != null &&
            unitsValue != null && unitsValue in minUnits..50

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add new item" else "Edit item") },
        text = {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Item name") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Box {
                    OutlinedButton(onClick = { typeMenuOpen = true }) {
                        Text("Type: ${selectedType?.name ?: "Choose"}")
                    }
                    DropdownMenu(expanded = typeMenuOpen,
                        onDismissRequest = { typeMenuOpen = false }) {
                        types.forEach { type ->
                            DropdownMenuItem(text = { Text(type.name) }, onClick = {
                                typeId = type.id
                                typeMenuOpen = false
                            })
                        }
                    }
                }
                Box {
                    OutlinedButton(onClick = { conditionMenuOpen = true }) {
                        Text("Condition: $condition")
                    }
                    DropdownMenu(expanded = conditionMenuOpen,
                        onDismissRequest = { conditionMenuOpen = false }) {
                        conditions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                condition = option
                                conditionMenuOpen = false
                            })
                        }
                    }
                }
                OutlinedTextField(value = location, onValueChange = { location = it },
                    label = { Text("Pick-up location") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it },
                    label = { Text("Description") }, minLines = 2,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = units, onValueChange = { units = it },
                    label = { Text("Number of units") },
                    supportingText = {
                        Text(if (onLoan > 0) "Between $minUnits and 50 ($onLoan on loan now)"
                        else "Between 1 and 50")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(enabled = canSave, onClick = {
                val validTypeId = selectedType?.id ?: return@TextButton
                val validUnits = units.toIntOrNull() ?: return@TextButton
                onSave(Item(
                    id = existing?.id ?: 0, typeId = validTypeId, name = name.trim(),
                    description = description.trim(), location = location.trim(),
                    condition = condition, unitsAvailable = validUnits,
                    unitsTotal = validUnits
                ))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
