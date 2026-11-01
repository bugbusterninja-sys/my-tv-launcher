package com.cleanyourtv.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalTvMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // Bumped on every resume so the app list refreshes after installs/uninstalls.
    private var refresh by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                var apps by remember { mutableStateOf(emptyList<AppItem>()) }

                LaunchedEffect(refresh) {
                    apps = withContext(Dispatchers.Default) {
                        AppRepository.loadApps(applicationContext)
                    }
                }

                LauncherScreen(apps = apps) { app ->
                    AppRepository.launch(this@MainActivity, app.packageName)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh++
    }
}
