package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.example.tusbookings.R
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(
    isBusy: Boolean,
    error: String?,
    onSignIn: (String, String) -> Unit,
    onCreateAccount: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.img_logo),
            contentDescription = "TusBookings logo",
            modifier = Modifier.size(96.dp)
        )
        Text("TusBookings", style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary)
        Text("Borrow college equipment in a few taps.")
        OutlinedTextField(email, { email = it }, label = { Text("College email") },
            enabled = !isBusy, singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(password, { password = it }, label = { Text("Password") },
            enabled = !isBusy, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            enabled = !isBusy && email.isNotBlank() && password.isNotBlank(),
            onClick = { onSignIn(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (isBusy) "Signing in…" else "Sign in") }
        TextButton(enabled = !isBusy, onClick = onCreateAccount) {
            Text("New here? Create an account")
        }
    }
}
