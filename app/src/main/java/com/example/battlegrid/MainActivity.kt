package com.example.battlegrid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.example.battlegrid.navigation.BattleGridNavigation
import com.example.battlegrid.ui.theme.BattleGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set your ArcGIS API key from BuildConfig
        val apiKey = BuildConfig.ARCGIS_API_KEY
        Log.d("BattleGrid", "API Key length: ${apiKey.length}")
        Log.d("BattleGrid", "API Key starts with: ${apiKey.take(10)}...")
        ArcGISEnvironment.apiKey = ApiKey.create(apiKey)
        
        enableEdgeToEdge()
        setContent {
            BattleGridTheme {
                BattleGridNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BattleGridPreview() {
    BattleGridTheme {
        BattleGridNavigation()
    }
}