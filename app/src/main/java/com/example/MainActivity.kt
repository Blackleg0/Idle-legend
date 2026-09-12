package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.idlelegends.audio.SoundManager
import com.example.idlelegends.ui.GameScreen
import com.example.idlelegends.ui.GameViewModel

private val FantasyDarkColors = darkColorScheme(
    primary = Color(0xFFFFD54F),
    onPrimary = Color.Black,
    secondary = Color(0xFF00E5FF),
    onSecondary = Color.Black,
    tertiary = Color(0xFF00E676),
    onTertiary = Color.Black,
    background = Color(0xFF101018),
    onBackground = Color.White,
    surface = Color(0xFF181924),
    onSurface = Color.White
)

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = FantasyDarkColors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.musicEnabled) {
            SoundManager.startBgm()
        }
    }

    override fun onPause() {
        super.onPause()
        SoundManager.stopBgm()
        viewModel.saveGame()
    }
}

