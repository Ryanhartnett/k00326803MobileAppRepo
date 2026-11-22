package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusbookings.model.LoanSummary

@Composable
fun MyLoansScreen(
    loans: List<LoanSummary>,
    isSaving: Boolean,
    message: String?,
    onReturn: (Int) -> Unit,
    onBrowse: () -> Unit
) {
    var loanToReturn by remember { mutableStateOf<LoanSummary?>(null) }
    val out = loans.filter { !it.returned }
    val history = loans.filter { it.returned }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("My loans", style = MaterialTheme.typography.headlineMedium) }
        message?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }

        if (loans.isEmpty()) {
            item {
                Text("You haven't borrowed anything yet.")
                Button(onClick = onBrowse) { Text("Browse equipment") }
            }
        }

        if (out.isNotEmpty()) {
            item { Text("Out now", style = MaterialTheme.typography.titleMedium) }
            items(out, key = { it.id }) { loan ->
                LoanCard(loan) {
                    Button(enabled = !isSaving, onClick = { loanToReturn = loan }) {
                        Text("Return")
                    }
                }
            }
        }

        if (history.isNotEmpty()) {
            item { Text("Returned", style = MaterialTheme.typography.titleMedium) }
            items(history, key = { it.id }) { loan ->
                LoanCard(loan) { Text("Returned ✓", color = MaterialTheme.colorScheme.primary) }
            }
        }
    }

    loanToReturn?.let { loan ->
        AlertDialog(
            onDismissRequest = { loanToReturn = null },
            title = { Text("Return item?") },
            text = { Text("Have you handed back ${loan.units} x ${loan.name}?") },
            confirmButton = {
                TextButton(enabled = !isSaving, onClick = {
                    onReturn(loan.id)
                    loanToReturn = null
                }) { Text("Yes, returned") }
            },
            dismissButton = {
                TextButton(onClick = { loanToReturn = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun LoanCard(loan: LoanSummary, action: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(loan.name, style = MaterialTheme.typography.titleMedium)
            Text("${loan.units} unit(s)  |  Return by ${loan.returnBy}")
            Text("For: ${loan.reason}", style = MaterialTheme.typography.bodySmall)
            action()
        }
    }
}
