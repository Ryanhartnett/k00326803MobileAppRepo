package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    email: String?,
    isAdmin: Boolean,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Signed in as", style = MaterialTheme.typography.labelMedium)
                Text(email ?: "Unknown", style = MaterialTheme.typography.titleMedium)
                Text(if (isAdmin) "Role: Admin" else "Role: Student")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark mode", modifier = Modifier.weight(1f))
            Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
        }
        Text("Your appearance choice is saved on this device.",
            style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = onSignOut) { Text("Sign out") }
    }
}
