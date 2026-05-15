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

    companion object {
        private val _callActionFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
        val callActionFlow = _callActionFlow.asSharedFlow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            VelstrackTheme {
                RootNavGraph()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getStringExtra("call_action") == "show_active_call") {
            val number = intent.getStringExtra("call_number") ?: "Unknown"
            _callActionFlow.tryEmit(number)
        }
    }
}
