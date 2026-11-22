package com.example.tusbookings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.tusbookings.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusbookings.ui.BookingsUiState
import com.example.tusbookings.ui.components.ItemCard

@Composable
fun HomeScreen(
    state: BookingsUiState,
    email: String?,
    onBrowse: () -> Unit,
    onMyLoans: () -> Unit,
    onItemOpen: (Int) -> Unit
) {
    // Items with one unit or fewer left - shown as "Going fast".
    val goingFast = state.items.filter { it.unitsAvailable <= 1 }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Box(
                Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.img_home_banner),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    Modifier.align(Alignment.CenterStart).padding(start = 20.dp)
                ) {
                    Text("Hi ${email?.substringBefore("@") ?: "there"}",
                        style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Text("Borrow. Create. Return.",
                        style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Items on loan: ${state.loansOut}",
                        style = MaterialTheme.typography.titleLarge)
                    Text("Equipment in the catalogue: ${state.items.size}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onBrowse) { Text("Browse equipment") }
                        OutlinedButton(onClick = onMyLoans) { Text("My loans") }
                    }
                }
            }
        }
        if (goingFast.isNotEmpty()) {
            item { Text("Going fast", style = MaterialTheme.typography.titleMedium) }
            items(goingFast, key = { it.id }) { item ->
                ItemCard(item = item, typeName = state.typeName(item.typeId),
                    onOpen = { onItemOpen(item.id) })
            }
        }
    }
}
