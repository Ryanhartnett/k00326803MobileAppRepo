package com.example.tusbookings.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tusbookings.R

// Each equipment type has its own coloured picture in res/drawable.
fun imageForType(typeId: Int): Int = when (typeId) {
    1 -> R.drawable.img_laptop
    2 -> R.drawable.img_camera
    3 -> R.drawable.img_audio
    4 -> R.drawable.img_tablet
    else -> R.drawable.img_accessory
}

@Composable
fun ItemImage(
    typeId: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(imageForType(typeId)),
        contentDescription = null,
        modifier = modifier.size(size).clip(RoundedCornerShape(size / 5))
    )
}
