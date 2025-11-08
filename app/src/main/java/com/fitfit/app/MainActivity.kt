package com.fitfit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.fitfit.app.data.local.userPrefsDataStore
import com.fitfit.app.navigation.AppNavigation
import com.fitfit.app.ui.theme.FitFitTheme
import kotlinx.coroutines.flow.map


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitFitTheme {
                val context = LocalContext.current
                val isLoggedInFlow = remember { context.userPrefsDataStore.data.map { it[booleanPreferencesKey("is_logged_in")] ?: false } }
                val isLoggedIn by isLoggedInFlow.collectAsState(initial = false)
                AppNavigation(isLoggedIn = isLoggedIn)
            }
        }
    }
}