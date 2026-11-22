package com.example.tusbookings

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tusbookings.ui.AuthViewModel
import com.example.tusbookings.ui.BookingsUiState
import com.example.tusbookings.ui.BookingsViewModel
import com.example.tusbookings.ui.components.BookingsMenuShell
import com.example.tusbookings.ui.screens.*

@Composable
fun TusBookingsApp(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val model: BookingsViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )
    val state by model.uiState.collectAsStateWithLifecycle()

    val authModel: AuthViewModel = viewModel()
    val authState by authModel.uiState.collectAsStateWithLifecycle()

    // Each signed-in user only sees their own loans.
    LaunchedEffect(authState.uid) { model.setUser(authState.uid) }

    var showSignUp by rememberSaveable { mutableStateOf(false) }
    val uid = authState.uid

    if (uid == null) {
        Surface(Modifier.fillMaxSize()) {
            if (showSignUp) {
                SignUpScreen(
                    isBusy = authState.isBusy,
                    error = authState.error,
                    onSignUp = authModel::signUp,
                    onBack = {
                        authModel.clearError()
                        showSignUp = false
                    }
                )
            } else {
                SignInScreen(
                    isBusy = authState.isBusy,
                    error = authState.error,
                    onSignIn = authModel::signIn,
                    onCreateAccount = {
                        authModel.clearError()
                        showSignUp = true
                    }
                )
            }
        }
    } else {
        // key(uid) resets navigation when a different user signs in.
        key(uid) {
            SignedInArea(
                state = state,
                model = model,
                email = authState.email,
                isAdmin = authState.roleReady && authState.isAdmin,
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onSignOut = { authModel.signOut() }
            )
        }
    }
}

@Composable
private fun SignedInArea(
    state: BookingsUiState,
    model: BookingsViewModel,
    email: String?,
    isAdmin: Boolean,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    fun openTop(route: String) {
        model.clearMessage()
        navController.navigate(route) {
            popUpTo("home") { inclusive = false }
            launchSingleTop = true
        }
    }

    fun openItem(id: Int) {
        model.clearMessage()
        navController.navigate("item/$id")
    }

    BookingsMenuShell(
        selectedRoute = currentRoute,
        enabled = !state.isLoading && state.loadError == null,
        isAdmin = isAdmin,
        onOpen = { route -> openTop(route) },
        onSignOut = onSignOut
    ) { padding ->
        if (state.isLoading) {
            Column(Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator()
                Text("Loading equipment…")
            }
        } else if (state.loadError != null) {
            Text(
                text = "Database error: ${state.loadError}",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") {
                    HomeScreen(
                        state = state,
                        email = email,
                        onBrowse = { openTop("browse") },
                        onMyLoans = { openTop("loans") },
                        onItemOpen = { id -> openItem(id) }
                    )
                }
                composable("browse") {
                    BrowseScreen(state = state, onItemOpen = { id -> openItem(id) })
                }
                composable("loans") {
                    MyLoansScreen(
                        loans = state.loans,
                        isSaving = state.isSaving,
                        message = state.message,
                        onReturn = model::returnLoan,
                        onBrowse = { openTop("browse") }
                    )
                }
                composable("manage") {
                    if (isAdmin) {
                        ManageItemsScreen(
                            state = state,
                            onAddItem = model::saveItem,
                            onUpdateItem = model::updateItem,
                            onDeleteItem = model::deleteItem,
                            onItemOpen = { id -> openItem(id) }
                        )
                    } else {
                        Text("Admins only", modifier = Modifier.padding(16.dp))
                    }
                }
                composable("settings") {
                    SettingsScreen(
                        email = email,
                        isAdmin = isAdmin,
                        darkTheme = darkTheme,
                        onDarkThemeChange = onDarkThemeChange,
                        onSignOut = onSignOut
                    )
                }
                composable(
                    route = "item/{itemId}",
                    arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                ) { entry ->
                    val item = state.findItem(entry.arguments?.getInt("itemId"))
                    if (item == null) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Item not found")
                            Button(onClick = { navController.popBackStack() }) { Text("Back") }
                        }
                    } else {
                        ItemDetailScreen(
                            item = item,
                            typeName = state.typeName(item.typeId),
                            isSaving = state.isSaving,
                            message = state.message,
                            onBorrow = { units, returnBy, reason ->
                                model.borrow(item, units, returnBy, reason)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
