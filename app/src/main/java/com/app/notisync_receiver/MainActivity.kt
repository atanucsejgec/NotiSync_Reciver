// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/MainActivity.kt
// Purpose: Single Activity hosting all Compose screens
// ============================================================

package com.app.notisync_receiver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                ReceiverNavGraph()
            }
        }
    }
}
