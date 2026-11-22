package com.example.tusbookings.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusbookings.model.Item

@Composable
fun ItemCard(
    item: Item,
    typeName: String,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onOpen,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ItemImage(typeId = item.typeId, size = 76.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(typeName.uppercase(), style = MaterialTheme.typography.labelMedium)
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("${item.location}  |  ${item.condition}",
                    style = MaterialTheme.typography.bodySmall)
                AvailabilityText(item)
            }
        }
    }
}

@Composable
fun AvailabilityText(item: Item) {
    if (item.unitsAvailable == 0) {
        Text(
            "All out on loan",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleSmall
        )
    } else {
        Text(
            "${item.unitsAvailable} of ${item.unitsTotal} available",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
