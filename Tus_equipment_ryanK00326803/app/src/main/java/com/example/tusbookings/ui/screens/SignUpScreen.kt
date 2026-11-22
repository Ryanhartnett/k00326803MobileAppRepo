package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    isBusy: Boolean,
    error: String?,
    onSignUp: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val mismatch = confirm.isNotEmpty() && password != confirm
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(email, { email = it }, label = { Text("College email") },
            enabled = !isBusy, singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(password, { password = it }, label = { Text("Password") },
            enabled = !isBusy, singleLine = true, modifier = Modifier.fillMaxWidth(),
            supportingText = { Text("At least 6 characters") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        OutlinedTextField(confirm, { confirm = it }, label = { Text("Confirm password") },
            enabled = !isBusy, singleLine = true, modifier = Modifier.fillMaxWidth(),
            isError = mismatch,
            supportingText = { if (mismatch) Text("Passwords don't match") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            enabled = !isBusy && email.isNotBlank() &&
                    password.length >= 6 && password == confirm,
            onClick = { onSignUp(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (isBusy) "Creating account…" else "Sign up") }
        TextButton(enabled = !isBusy, onClick = onBack) { Text("Back to sign in") }
    }
}
