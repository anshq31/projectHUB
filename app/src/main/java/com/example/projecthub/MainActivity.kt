package com.example.projecthub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.projecthub.navigation.appNavigation
import com.example.projecthub.ui.theme.ProjectHUBTheme
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val authViewModel: authViewModel by viewModels()

    // Remove: private var deepLinkRouteState: MutableState<String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Use remember so state survives recomposition
            val deepLinkRouteState = remember { mutableStateOf(getRouteFromIntent(intent)) }

            ProjectHUBTheme(themeViewModel) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    appNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel,
                        deepLinkRoute = deepLinkRouteState.value
                    )
                }
            }

            // Side effect to update the state if a new intent is received
            DisposableEffect(Unit) {
                val activity = this@MainActivity
                activity.deepLinkRouteHolder = deepLinkRouteState
                onDispose {
                    activity.deepLinkRouteHolder = null
                }
            }
        }
    }

    // Hold a reference so we can update Compose state from onNewIntent
    private var deepLinkRouteHolder: MutableState<String?>? = null

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val route = getRouteFromIntent(intent)
        deepLinkRouteHolder?.value = route
    }

    private fun getRouteFromIntent(intent: Intent?): String? {
        intent?.let { notifIntent ->
            return when (notifIntent.getStringExtra("notification_type")) {
                "chat_message" -> notifIntent.getStringExtra("channelId")?.let { "chat_screen/$it" }
                "new_bid", "bid_accepted", "user_rated" -> notifIntent.getStringExtra("assignmentId")?.let { "assignment_detail_screen/$it" }
                else -> null
            }
        }
        return null
    }
}