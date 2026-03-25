package com.groze.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.groze.app.data.preferences.UserPreferences
import com.groze.app.navigation.GrozeNavHost
import com.groze.app.ui.theme.GrozeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val hasSeenOnboarding by userPreferences.hasSeenOnboarding.collectAsState(initial = true)
            val darkModePreference by userPreferences.darkMode.collectAsState(initial = "system")
            val configuration = LocalConfiguration.current
            
            val isDarkTheme = when (darkModePreference) {
                "dark" -> true
                "light" -> false
                else -> configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_MASK
            }

            GrozeTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                    GrozeNavHost(startOnboarding = !hasSeenOnboarding)
                }
            }
        }
    }
}
