package com.example.battlegrid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.battlegrid.navigation.BattleGridNavigation
import com.example.battlegrid.ui.theme.BattleGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("BattleGrid", "MainActivity created - Google Maps will initialize automatically")
        
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