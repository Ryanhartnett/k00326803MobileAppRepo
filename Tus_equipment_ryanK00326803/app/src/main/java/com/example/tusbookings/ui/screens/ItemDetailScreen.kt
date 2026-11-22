package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusbookings.model.Item
import com.example.tusbookings.ui.components.AvailabilityText
import com.example.tusbookings.ui.components.ItemImage

const val MAX_UNITS_PER_LOAN = 3

@Composable
fun ItemDetailScreen(
    item: Item,
    typeName: String,
    isSaving: Boolean,
    message: String?,
    onBorrow: (units: Int, returnBy: String, reason: String) -> Unit,
    onBack: () -> Unit
) {
    // rememberSaveable keeps the form filled in when the phone is rotated.
    var units by rememberSaveable { mutableStateOf(1) }
    var returnBy by rememberSaveable { mutableStateOf("") }
    var reason by rememberSaveable { mutableStateOf("") }
    var triedToSubmit by rememberSaveable { mutableStateOf(false) }

    val maxUnits = minOf(MAX_UNITS_PER_LOAN, item.unitsAvailable)
    // Keeps the chosen number inside 1..maxUnits if stock changes.
    val chosenUnits = units.coerceAtMost(maxUnits).coerceAtLeast(1)
    val returnError = triedToSubmit && returnBy.isBlank()
    val reasonError = triedToSubmit && reason.trim().length < 5

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBack) { Text("Back") }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ItemImage(typeId = item.typeId, size = 180.dp)
        }
        Text(typeName.uppercase(), style = MaterialTheme.typography.labelLarge)
        Text(item.name, style = MaterialTheme.typography.headlineMedium)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pick up from: ${item.location}")
                Text("Condition: ${item.condition}")
                AvailabilityText(item)
            }
        }
        Text(item.description)

        HorizontalDivider()
        Text("Borrow this item", style = MaterialTheme.typography.titleLarge)

        if (maxUnits == 0) {
            Text("Every unit is out on loan right now. Check back later.",
                color = MaterialTheme.colorScheme.error)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Units:")
                OutlinedButton(enabled = chosenUnits > 1, onClick = { units = chosenUnits - 1 }) { Text("-") }
                Text("$chosenUnits", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(enabled = chosenUnits < maxUnits, onClick = { units = chosenUnits + 1 }) { Text("+") }
            }
            Text("Max $maxUnits per loan", style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(
                value = returnBy, onValueChange = { returnBy = it },
                label = { Text("Return by") }, singleLine = true,
                placeholder = { Text("e.g. Fri 28 Nov") },
                isError = returnError,
                supportingText = { if (returnError) Text("Enter a return date") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = reason, onValueChange = { reason = it },
                label = { Text("What is it for?") }, minLines = 2,
                isError = reasonError,
                supportingText = { if (reasonError) Text("Give a short reason (5+ characters)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    triedToSubmit = true
                    if (returnBy.isNotBlank() && reason.trim().length >= 5) {
                        onBorrow(chosenUnits, returnBy, reason)
                        returnBy = ""
                        reason = ""
                        units = 1
                        triedToSubmit = false
                    }
                }
            ) { Text(if (isSaving) "Saving…" else "Confirm loan") }
        }
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}
