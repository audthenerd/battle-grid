package com.example.battlegrid.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.battlegrid.database.BattleGridDatabase
import com.example.battlegrid.repository.PolygonRepository
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        // Title
        Text(
            text = "Battle Grid",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        // Map container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            GoogleMapView(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun GoogleMapView(modifier: Modifier = Modifier) {
    // San Francisco coordinates as default location
    val sanFrancisco = LatLng(37.7749, -122.4194)
    
    val context = LocalContext.current
    val database = remember { BattleGridDatabase.getDatabase(context) }
    val polygonRepository = remember { PolygonRepository(database.polygonDao()) }
    val scope = rememberCoroutineScope()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sanFrancisco, 15f)
    }
    
    // Polygon drawing state
    var isDrawingMode by remember { mutableStateOf(false) }
    var currentPolygonVertices by remember { mutableStateOf(listOf<LatLng>()) }
    
    // Load polygons from database
    val savedPolygons by polygonRepository.getAllVisiblePolygons().collectAsState(initial = emptyList())
    
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            mapToolbarEnabled = false
        )
    }
    
    val mapProperties = remember {
        MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = false
        )
    }

    Log.d("BattleGrid", "Rendering Google Map with polygon drawing")
    
    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties,
            onMapClick = { latLng ->
                if (isDrawingMode) {
                    currentPolygonVertices = currentPolygonVertices + latLng
                    Log.d("BattleGrid", "Added vertex: $latLng")
                }
            }
        ) {
            // Draw saved polygons from database
            savedPolygons.forEach { savedPolygon ->
                val vertices = polygonRepository.parseCoordinates(savedPolygon.coordinates)
                if (vertices.size >= 3) {
                    Polygon(
                        points = vertices,
                        fillColor = Color(android.graphics.Color.parseColor(savedPolygon.fillColor)).copy(alpha = savedPolygon.fillAlpha),
                        strokeColor = Color(android.graphics.Color.parseColor(savedPolygon.strokeColor)),
                        strokeWidth = savedPolygon.strokeWidth,
                        tag = "saved_polygon_${savedPolygon.id}",
                        onClick = { 
                            // Delete polygon on click
                            scope.launch {
                                try {
                                    polygonRepository.deletePolygonById(savedPolygon.id)
                                    Log.d("BattleGrid", "Polygon deleted from database")
                                } catch (e: Exception) {
                                    Log.e("BattleGrid", "Error deleting polygon: ${e.message}")
                                }
                            }
                        }
                    )
                }
            }
            
            // Draw current polygon being drawn
            if (currentPolygonVertices.size >= 3) {
                Polygon(
                    points = currentPolygonVertices,
                    fillColor = Color.Red.copy(alpha = 0.3f),
                    strokeColor = Color.Red,
                    strokeWidth = 3f,
                    tag = "current_polygon"
                )
            }
            
            // Draw markers for current polygon vertices
            currentPolygonVertices.forEachIndexed { index, vertex ->
                Marker(
                    state = MarkerState(position = vertex),
                    title = "Vertex ${index + 1}",
                    tag = "vertex_$index"
                )
            }
        }
        
        // Drawing controls overlay - simplified to just edit icon
        FloatingActionButton(
            onClick = { 
                isDrawingMode = !isDrawingMode
                if (!isDrawingMode && currentPolygonVertices.size >= 3) {
                    // Save current polygon to database
                    scope.launch {
                        try {
                            polygonRepository.savePolygon(
                                coordinates = currentPolygonVertices,
                                fillColor = "#3300FF", // Blue fill
                                strokeColor = "#0000FF", // Blue stroke
                                fillAlpha = 0.3f
                            )
                            currentPolygonVertices = listOf()
                            Log.d("BattleGrid", "Polygon saved to database")
                        } catch (e: Exception) {
                            Log.e("BattleGrid", "Error saving polygon: ${e.message}")
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            containerColor = if (isDrawingMode) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = if (isDrawingMode) "Finish drawing" else "Start drawing",
                tint = if (isDrawingMode) MaterialTheme.colorScheme.onError 
                      else MaterialTheme.colorScheme.onPrimary
            )
        }
        
        // Status indicator
        if (isDrawingMode) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Drawing Mode - Tap map to add vertices (${currentPolygonVertices.size} points)",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Polygon info
        if (savedPolygons.isNotEmpty() || currentPolygonVertices.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Saved Polygons: ${savedPolygons.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (currentPolygonVertices.isNotEmpty()) {
                        Text(
                            text = "Current: ${currentPolygonVertices.size} vertices",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
} 