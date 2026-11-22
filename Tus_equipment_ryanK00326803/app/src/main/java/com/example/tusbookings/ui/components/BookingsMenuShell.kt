package com.example.tusbookings.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsMenuShell(
    selectedRoute: String?,
    enabled: Boolean,
    isAdmin: Boolean,
    onOpen: (String) -> Unit,
    onSignOut: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menu = buildList {
        add("home" to "Home")
        add("browse" to "Browse equipment")
        add("loans" to "My loans")
        if (isAdmin) add("manage" to "Manage equipment (admin)")
        add("settings" to "Settings")
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = enabled,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "TusBookings",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                menu.forEach { (route, label) ->
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = selectedRoute == route,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = {
                            if (enabled) {
                                scope.launch {
                                    drawerState.close()
                                    onOpen(route)
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TusBookings") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    navigationIcon = {
                        IconButton(
                            enabled = enabled,
                            modifier = Modifier.semantics {
                                contentDescription = "Open navigation drawer"
                            },
                            onClick = { scope.launch { drawerState.open() } }
                        ) { Text("☰") }
                    },
                    actions = {
                        TextButton(onClick = onSignOut) { Text("Sign out") }
                    }
                )
            }
        ) { padding ->
            content(padding)
        }
    }

    // Consume Back while the drawer is open; keep the current screen.
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
}
