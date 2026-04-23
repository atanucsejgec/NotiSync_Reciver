// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/MainActivity.kt
// Purpose: Single Activity hosting all Compose screens
// ============================================================

package com.app.notisync_receiver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.notisync_receiver.ui.navigation.ReceiverNavGraph
import com.app.notisync_receiver.ui.theme.NotiSyncReceiverTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotiSyncReceiverTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReceiverAppContent(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiverAppContent(modifier: Modifier = Modifier) {
    ReceiverNavGraph(modifier = modifier)
}