package com.example.idlelegends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.ui.battle.BattlefieldView
import com.example.idlelegends.ui.components.EquipmentDetailDialog
import com.example.idlelegends.ui.components.GameTopBar
import com.example.idlelegends.ui.components.OfflineRewardsDialog
import com.example.idlelegends.ui.tabs.BattleTab
import com.example.idlelegends.ui.tabs.HeroTab
import com.example.idlelegends.ui.tabs.InventoryTab
import com.example.idlelegends.ui.tabs.SkillsTab
import com.example.idlelegends.ui.tabs.WorldsTab

enum class GameTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    BATTLE("Battle", Icons.Default.SportsKabaddi),
    HERO("Hero", Icons.Default.Person),
    INVENTORY("Bag", Icons.Default.Backpack),
    SKILLS("Skills", Icons.Default.Bolt),
    WORLDS("Worlds", Icons.Default.Public)
}

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(GameTab.BATTLE) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = Color(0xFF101018),
        topBar = {
            GameTopBar(
                playerLevel = uiState.playerLevel,
                gold = uiState.gold,
                gems = uiState.gems,
                soundEnabled = uiState.soundEnabled,
                musicEnabled = uiState.musicEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onToggleMusic = { viewModel.toggleMusic() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF14151F),
                tonalElevation = 8.dp
            ) {
                GameTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Black else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color(0xFFFFD54F),
                            indicatorColor = Color(0xFFFFD54F),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Persistent Real-Time Battlefield on top
            BattlefieldView(
                world = uiState.currentWorld,
                stage = uiState.currentStage,
                wave = uiState.currentWave,
                heroClass = uiState.selectedHeroClass,
                heroLevel = uiState.playerLevel,
                heroHp = uiState.heroCurrentHp,
                heroMaxHp = uiState.heroMaxHp,
                enemy = uiState.currentEnemy,
                heroAttacking = uiState.heroAttacking,
                enemyAttacking = uiState.enemyAttacking,
                heroHit = uiState.heroHit,
                enemyHit = uiState.enemyHit,
                damageNumbers = uiState.damageNumbers,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )

            // Tab Content Below Battlefield
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (selectedTab) {
                    GameTab.BATTLE -> {
                        BattleTab(
                            uiState = uiState,
                            onCastSkill = { viewModel.castSkill(it) },
                            onToggleAutoSkill = { viewModel.setAutoSkill(it) },
                            onToggleAutoAdvance = { viewModel.setAutoAdvance(it) },
                            onUpgradeStat = { viewModel.upgradeStat(it) },
                            onClearLogs = { viewModel.clearCombatLogs() }
                        )
                    }
                    GameTab.HERO -> {
                        HeroTab(
                            currentHeroClass = uiState.selectedHeroClass,
                            unlockedHeroes = uiState.unlockedHeroes,
                            playerGems = uiState.gems,
                            playerLevel = uiState.playerLevel,
                            playerXp = uiState.playerXp,
                            maxXp = uiState.xpForNextLevel,
                            onSelectHero = { viewModel.selectHero(it) },
                            onUnlockHero = { viewModel.unlockHero(it) }
                        )
                    }
                    GameTab.INVENTORY -> {
                        InventoryTab(
                            equipmentList = uiState.equipmentList,
                            onItemClick = { viewModel.showItemDetail(it) },
                            onAutoEquipBest = { viewModel.autoEquipBest() }
                        )
                    }
                    GameTab.SKILLS -> {
                        SkillsTab(
                            skills = uiState.activeSkills,
                            playerGold = uiState.gold,
                            onUpgradeSkill = { viewModel.upgradeSkill(it) }
                        )
                    }
                    GameTab.WORLDS -> {
                        WorldsTab(
                            currentStage = uiState.currentStage,
                            highestUnlockedStage = uiState.highestUnlockedStage,
                            onSelectStage = { viewModel.selectStage(it) }
                        )
                    }
                }
            }
        }
    }

    // Offline Rewards Dialog
    uiState.offlineRewardSummary?.let { summary ->
        OfflineRewardsDialog(
            summary = summary,
            onClaim = { viewModel.claimOfflineRewards() }
        )
    }

    // Equipment Detail / Upgrade Dialog
    uiState.selectedItemForDetail?.let { item ->
        EquipmentDetailDialog(
            item = item,
            playerGold = uiState.gold,
            onEquipToggle = { viewModel.toggleEquip(it) },
            onUpgrade = { viewModel.upgradeEquipment(it) },
            onDismantle = { viewModel.dismantleEquipment(it) },
            onDismiss = { viewModel.showItemDetail(null) }
        )
    }
}
