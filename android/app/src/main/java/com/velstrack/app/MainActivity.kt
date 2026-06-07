package com.velstrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.velstrack.app.core.theme.VelstrackTheme
import com.velstrack.app.presentation.RootNavGraph
import dagger.hilt.android.AndroidEntryPoint

import android.content.Intent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Removed callActionFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VelstrackTheme {
                RootNavGraph()
            }
        }
    }
}
