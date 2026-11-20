package com.iptvhero.pro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.iptvhero.pro.ui.screens.MainScreen
import com.iptvhero.pro.ui.screens.PlayerScreen
import com.iptvhero.pro.ui.screens.ProfilesScreen
import com.iptvhero.pro.ui.screens.SplashScreen
import com.iptvhero.pro.ui.theme.HeroThemeType
import com.iptvhero.pro.ui.viewmodel.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf("splash") }
    var currentTheme by remember { mutableStateOf(HeroThemeType.DUEL) }
    val profiles by viewModel.profiles.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()

    fun cycleTheme() {
        val values = HeroThemeType.entries.toTypedArray()
        currentTheme = values[(currentTheme.ordinal + 1) % values.size]
    }

    val statusText = uiState.errorMessage ?: uiState.successMessage

    when (currentScreen) {
        "splash" -> {
            SplashScreen(onFinished = { currentScreen = "profiles" })
        }
        "profiles" -> {
            ProfilesScreen(
                profiles = profiles,
                activeProfileId = viewModel.activeProfile?.id,
                statusMessage = statusText,
                pendingHeroMessage = uiState.pendingHeroMessage,
                currentTheme = currentTheme,
                onCycleTheme = ::cycleTheme,
                onOpenImportTxt = { currentScreen = "main" },
                onLoadHeroGithub = {
                    viewModel.loadHeroListFromGithub()
                },
                onConfirmPendingHero = {
                    viewModel.confirmPendingHeroList()
                },
                onDismissPendingHero = {
                    viewModel.dismissPendingHeroList()
                },
                onSelectProfile = { profileId ->
                    viewModel.selectProfile(profileId)
                },
                onDeleteProfile = { profileId ->
                    viewModel.deleteProfile(profileId)
                },
                onClearAllProfiles = { viewModel.clearAllProfiles() },
                onEnterApp = { currentScreen = "main" }
            )
        }
        "main" -> {
            MainScreen(
                viewModel = viewModel,
                currentTheme = currentTheme,
                onCycleTheme = ::cycleTheme,
                onBackToProfiles = { currentScreen = "profiles" },
                onNavigateToFullPlayer = { currentScreen = "player" }
            )
        }
        "player" -> {
            PlayerScreen(
                streamUrl = uiState.currentStreamUrl,
                channelTitle = uiState.currentChannelTitle,
                onBack = { currentScreen = "main" }
            )
        }
    }
}
