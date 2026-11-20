package com.iptvhero.pro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iptvhero.pro.ui.components.ProfileManagerDialog
import com.iptvhero.pro.ui.components.heroFocusable
import com.iptvhero.pro.ui.theme.DcBlue
import com.iptvhero.pro.ui.theme.DcCyan
import com.iptvhero.pro.ui.theme.GoldFocus
import com.iptvhero.pro.ui.theme.HeroThemeType
import com.iptvhero.pro.ui.theme.MarvelRed
import com.iptvhero.pro.ui.theme.MarvelYellow
import com.iptvhero.pro.ui.theme.TextDim
import com.iptvhero.pro.ui.theme.TextPrimary
import com.iptvhero.pro.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    currentTheme: HeroThemeType = HeroThemeType.DUEL,
    onCycleTheme: () -> Unit = {},
    onBackToProfiles: () -> Unit = {},
    onNavigateToFullPlayer: () -> Unit = {}
) {
    val profiles by viewModel.profiles.collectAsState(initial = emptyList())
    val channels by viewModel.channels.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val activeProfile = viewModel.activeProfile

    var selectedTab by remember { mutableStateOf("CANAIS") }
    var selectedCategory by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showCategoriesDialog by remember { mutableStateOf(false) }
    var selectedSeasonKey by remember { mutableStateOf<String?>(null) }

    val tabs = listOf("CANAIS", "FILMES", "SERIES")
    val tabChannels = remember(channels, selectedTab) {
        when (selectedTab) {
            "FILMES" -> channels.filter { it.contentType.equals("VOD", ignoreCase = true) }
            "SERIES" -> channels.filter { it.contentType.equals("SERIES", ignoreCase = true) }
            else -> channels.filter { it.contentType.equals("LIVE", ignoreCase = true) }
        }
    }

    val availableCategories = remember(tabChannels) {
        tabChannels.groupingBy { it.groupName.ifBlank { "Geral" } }.eachCount().toSortedMap()
    }

    val filteredChannels = remember(tabChannels, selectedCategory, searchQuery) {
        val byCat = if (selectedCategory.isBlank()) tabChannels
        else tabChannels.filter { it.groupName.equals(selectedCategory, ignoreCase = true) }
        if (searchQuery.isBlank()) byCat
        else byCat.filter { it.title.contains(searchQuery.trim(), ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(currentTheme.backgroundBrush)
        ) {
            // 1. TOP HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(currentTheme.headerBrush)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .heroFocusable(RoundedCornerShape(10.dp)) { onBackToProfiles() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "IPTV HERO",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 2.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("<", color = MarvelYellow, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x8C0A0A16))
                        .heroFocusable(CircleShape) { showProfileDialog = true }
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text("LISTAS / M3U / XTREAM", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x800A0A16))
                            .heroFocusable(CircleShape) { onCycleTheme() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("T", color = MarvelYellow, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x8C0A0A16))
                            .heroFocusable(RoundedCornerShape(10.dp)) { onBackToProfiles() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = activeProfile?.name ?: "PERFIL",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2. SECOND BAR: CANAIS | FILMES | SERIES | CATEGORIAS | PESQUISA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xE00E0E1A))
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldFocus.copy(alpha = 0.18f) else Color.Transparent)
                            .heroFocusable(RoundedCornerShape(8.dp)) {
                                selectedTab = tab
                                selectedCategory = ""
                                searchQuery = ""
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) GoldFocus else TextDim,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .heroFocusable(RoundedCornerShape(8.dp)) { showCategoriesDialog = true }
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = if (selectedCategory.isBlank()) "Categorias (${availableCategories.size})" else "Cat: $selectedCategory",
                        color = if (selectedCategory.isBlank()) TextPrimary else GoldFocus,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("PESQUISA", color = TextDim, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.width(240.dp).height(48.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldFocus,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // 3. MAIN GRID
            if (filteredChannels.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage
                            ?: if (uiState.isLoading) (uiState.successMessage ?: "Baixando / processando lista...")
                            else if (activeProfile == null) "Nenhum perfil ativo. Clique em HERO LISTA para importar."
                            else "Perfil '${activeProfile.name}' sem itens nesta aba.",
                        color = if (uiState.errorMessage != null) MarvelYellow else TextDim,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(filteredChannels, key = { _, ch -> ch.id }) { index, channel ->
                        val badgeText = when (channel.contentType.uppercase()) {
                            "VOD" -> "FILME"
                            "SERIES" -> "SERIE"
                            else -> "AO VIVO"
                        }

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x8C0E0E1C))
                                .heroFocusable(RoundedCornerShape(12.dp)) {
                                    if (channel.contentType.equals("SERIES", ignoreCase = true) && !channel.remoteId.isNullOrBlank()) {
                                        selectedSeasonKey = null
                                        viewModel.openSeriesSeasons(channel)
                                    } else {
                                        viewModel.setStreamUrl(channel.url, channel.title)
                                        onNavigateToFullPlayer()
                                    }
                                }
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 10f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(MarvelRed.copy(alpha = 0.35f), DcBlue.copy(alpha = 0.35f))
                                        )
                                    )
                            ) {
                                Text(
                                    text = channel.title.trim().take(1).uppercase().ifEmpty { "${index + 1}" },
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.Black.copy(alpha = 0.78f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        color = GoldFocus,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Text(
                                text = channel.title,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }

            // 4. BOTTOM FOOTER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xEB0A0A16))
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.horizontalGradient(listOf(MarvelRed, DcBlue)))
                        .heroFocusable(RoundedCornerShape(10.dp)) { viewModel.loadHeroListFromGithub() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("HERO LISTA", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .heroFocusable(CircleShape) {
                            activeProfile?.let { viewModel.selectProfile(it.id) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("R", color = GoldFocus, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }

                Text(
                    text = uiState.errorMessage
                        ?: uiState.successMessage
                        ?: "Itens: ${filteredChannels.size} | Perfil: ${activeProfile?.name ?: "Nenhum"}",
                    color = if (uiState.errorMessage != null) MarvelYellow else TextDim,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xE6161628))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "CONEXÕES: ${activeProfile?.activeConnections ?: 0}/${activeProfile?.maxConnections ?: "∞"}",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DcBlue.copy(alpha = 0.25f))
                        .heroFocusable(RoundedCornerShape(10.dp)) { onNavigateToFullPlayer() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text("[ ]", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }

        // OVERLAY DE TEMPORADAS / EPISODIOS DE SERIES (Identico ao #seasons-panel do index_2.html)
        if (uiState.selectedSeriesTitle.isNotBlank()) {
            val seasonsKeys = uiState.seasonsMap.keys.toList()
            val activeSeason = selectedSeasonKey ?: seasonsKeys.firstOrNull()
            val episodes = activeSeason?.let { uiState.seasonsMap[it] }.orEmpty()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xF705050A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(currentTheme.headerBrush)
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .heroFocusable(RoundedCornerShape(10.dp)) { viewModel.closeSeriesSeasons() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("< VOLTAR", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }

                    Text(
                        text = uiState.selectedSeriesTitle,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (uiState.isLoadingSeasons) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Carregando temporadas...", color = TextDim, fontSize = 14.sp)
                    }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .width(230.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF0E0E1A))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(seasonsKeys) { seasonNum ->
                                val isSel = seasonNum == activeSeason
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) DcBlue.copy(alpha = 0.45f) else Color(0xD614142A))
                                        .heroFocusable(RoundedCornerShape(10.dp)) { selectedSeasonKey = seasonNum }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Temporada $seasonNum", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${uiState.seasonsMap[seasonNum]?.size ?: 0}", color = DcCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(episodes) { ep ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xD614142A))
                                        .heroFocusable(RoundedCornerShape(10.dp)) {
                                            val url = viewModel.buildEpisodeStreamUrl(ep)
                                            if (url != null) {
                                                val fullTitle = "${uiState.selectedSeriesTitle} — T${activeSeason}E${ep.episodeNum.toString().padStart(2, '0')} — ${ep.title}"
                                                viewModel.closeSeriesSeasons()
                                                viewModel.setStreamUrl(url, fullTitle)
                                                onNavigateToFullPlayer()
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "E${ep.episodeNum.toString().padStart(2, '0')} — ${ep.title}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "T$activeSeason • ${ep.extension.uppercase()}${ep.duration?.let { " • $it" } ?: ""}",
                                        color = TextDim,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCategoriesDialog) {
        AlertDialog(
            onDismissRequest = { showCategoriesDialog = false },
            title = { Text("Categorias — $selectedTab") },
            text = {
                LazyColumn(
                    modifier = Modifier.height(340.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory.isBlank()) GoldFocus.copy(alpha = 0.25f) else Color.Transparent)
                                .heroFocusable(RoundedCornerShape(8.dp)) {
                                    selectedCategory = ""
                                    showCategoriesDialog = false
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Todas", fontWeight = FontWeight.Bold)
                            Text("${tabChannels.size}")
                        }
                    }
                    items(availableCategories.entries.toList()) { (catName, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory == catName) GoldFocus.copy(alpha = 0.25f) else Color.Transparent)
                                .heroFocusable(RoundedCornerShape(8.dp)) {
                                    selectedCategory = catName
                                    showCategoriesDialog = false
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(catName, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text("$count")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoriesDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }

    if (showProfileDialog) {
        ProfileManagerDialog(
            profiles = profiles,
            activeProfileId = activeProfile?.id,
            onDismiss = { showProfileDialog = false },
            onActivateProfile = { viewModel.selectProfile(it) },
            onDeleteProfile = { viewModel.deleteProfile(it) },
            onImportContent = { txtContent ->
                viewModel.importBatchTxt(txtContent)
                showProfileDialog = false
            },
            onImportGithubHero = { url ->
                viewModel.loadHeroListFromGithub(url)
                showProfileDialog = false
            },
            onImportXtream = { server, user, pass ->
                viewModel.importXtream(server, user, pass)
                showProfileDialog = false
            }
        )
    }
}
