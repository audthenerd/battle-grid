package com.example.battlegrid.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.toolkit.geoviewcompose.MapView

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        // Title
        Text(
            text = "Battle Grid",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        // Map taking up most of the screen
        ArcGISMapView(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}

@Composable
fun ArcGISMapView(modifier: Modifier = Modifier) {
    val map = remember {
        Log.d("BattleGrid", "Creating basic ArcGIS Map without basemap")
        // Create a simple empty map to test basic functionality
        ArcGISMap().apply {
            initialViewpoint = Viewpoint(
                latitude = 37.7749,
                longitude = -122.4194,
                scale = 100000.0
            )
            Log.d("BattleGrid", "Basic map created successfully")
        }
    }

    Log.d("BattleGrid", "Rendering MapView...")
    
    MapView(
        modifier = modifier,
        arcGISMap = map
    )
} 