package com.fitfit.app.ui.screen.outfitsScreen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OutfitsFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(64.dp),
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Outfits",
            modifier = Modifier.size(32.dp)
        )
    }
}