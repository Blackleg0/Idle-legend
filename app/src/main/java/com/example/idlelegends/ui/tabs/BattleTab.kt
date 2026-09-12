package com.example.idlelegends.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.model.CombatLog
import com.example.idlelegends.model.HeroSkill
import com.example.idlelegends.ui.GameUiState
import com.example.idlelegends.ui.battle.HeroStatBadge
import com.example.idlelegends.ui.battle.LogFilterCategory
import com.example.idlelegends.ui.battle.RealTimeCombatLogCard
import java.util.Locale

enum class BattleTabSubMode(val title: String, val icon: ImageVector) {
    COMBAT_LOG("Combat Log", Icons.Default.ReceiptLong),
    ATTRIBUTES("Attributes", Icons.Default.Bolt)
}

@Composable
fun BattleTab(
    uiState: GameUiState,
    onCastSkill: (HeroSkill) -> Unit,
    onToggleAutoSkill: (Boolean) -> Unit,
    onToggleAutoAdvance: (Boolean) -> Unit,
    onUpgradeStat: (String) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var subMode by remember { mutableStateOf(BattleTabSubMode.COMBAT_LOG) }
    var selectedFilter by remember { mutableStateOf(LogFilterCategory.ALL) }
    var autoScrollEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mode Switcher & Toggles Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sub-mode pill switcher
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF181926),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E324A))
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    BattleTabSubMode.values().forEach { mode ->
                        val isSelected = subMode == mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFFFD54F) else Color.Transparent,
                            modifier = Modifier
                                .clickable { subMode = mode }
                                .testTag("submode_${mode.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = mode.icon,
                                    contentDescription = mode.title,
                                    tint = if (isSelected) Color.Black else Color.Gray,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mode.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else Color(0xFFB0BEC5)
                                )
                            }
                        }
                    }
                }
            }

            // Auto-toggles
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Auto Skills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1F2B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Auto", fontSize = 10.sp, color = if (uiState.autoSkillEnabled) Color(0xFF00E5FF) else Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = uiState.autoSkillEnabled,
                        onCheckedChange = onToggleAutoSkill,
                        modifier = Modifier.size(24.dp).testTag("auto_skill_switch")
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Auto Advance
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1F2B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Stage", fontSize = 10.sp, color = if (uiState.autoAdvanceEnabled) Color(0xFF00E676) else Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = uiState.autoAdvanceEnabled,
                        onCheckedChange = onToggleAutoAdvance,
                        modifier = Modifier.size(24.dp).testTag("auto_advance_switch")
                    )
                }
            }
        }

        // Active Skills Cast Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.activeSkills.forEach { skill ->
                val isReady = skill.currentCooldown <= 0f
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable(enabled = isReady) { onCastSkill(skill) }
                        .testTag("cast_skill_${skill.id}"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isReady) Color(0xFF28293D) else Color(0xFF141520),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isReady) Color(0xFF00E5FF) else Color(0xFF2E324A)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = skill.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isReady) Color(0xFF00E5FF) else Color.Gray,
                            maxLines = 1
                        )
                        if (isReady) {
                            Text(
                                text = "READY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E676)
                            )
                        } else {
                            Text(
                                text = String.format(Locale.US, "%.1fs", skill.currentCooldown),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF8A80)
                            )
                        }
                    }
                }
            }
        }

        when (subMode) {
            BattleTabSubMode.COMBAT_LOG -> {
                // Battle Progress summary bar (Wave 1..5 Stepper & Hero Stats)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF181926),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E324A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Waves Progress Nodes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (w in 1..5) {
                                val isCompleted = w < uiState.currentWave
                                val isCurrent = w == uiState.currentWave
                                val isBossWave = w == 5

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isCompleted -> Color(0xFF00E676)
                                                    isCurrent && isBossWave -> Color(0xFFFF1744)
                                                    isCurrent -> Color(0xFFFFD54F)
                                                    else -> Color(0xFF222436)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(11.dp))
                                        } else if (isBossWave) {
                                            Text("👑", fontSize = 9.sp)
                                        } else {
                                            Text("$w", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isCurrent) Color.Black else Color.Gray)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isBossWave) "BOSS" else "W$w",
                                        fontSize = 8.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) Color.White else Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Live Hero Combat Attributes Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            HeroStatBadge("Atk", "${uiState.heroTotalAttack}", Icons.Default.Bolt, Color(0xFFFF5252), Modifier.weight(1f))
                            HeroStatBadge("Def", "${uiState.heroTotalDefense}", Icons.Default.Shield, Color(0xFF448AFF), Modifier.weight(1f))
                            HeroStatBadge("Spd", String.format(Locale.US, "%.2f/s", uiState.heroAttackSpeed), Icons.Default.FlashOn, Color(0xFFFFD600), Modifier.weight(1f))
                            HeroStatBadge("Crit", String.format(Locale.US, "%.0f%%", uiState.heroCritRate * 100), Icons.Default.Star, Color(0xFFE040FB), Modifier.weight(1f))
                        }
                    }
                }

                // Full Real-Time Combat Log
                RealTimeCombatLogCard(
                    combatLogs = uiState.combatLogs,
                    selectedFilter = selectedFilter,
                    onSelectFilter = { selectedFilter = it },
                    autoScrollEnabled = autoScrollEnabled,
                    onToggleAutoScroll = { autoScrollEnabled = it },
                    onClearLogs = onClearLogs,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            BattleTabSubMode.ATTRIBUTES -> {
                // STAT UPGRADES LIST
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "HERO ATTRIBUTES TRAINING",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 1.sp
                        )
                    }

                    item {
                        val strCost = (uiState.strengthLevel + 1) * 25L
                        StatUpgradeRow(
                            name = "Strength",
                            desc = "+4 Attack Power",
                            level = uiState.strengthLevel,
                            cost = strCost,
                            canAfford = uiState.gold >= strCost,
                            icon = Icons.Default.Bolt,
                            iconColor = Color(0xFFFF5252),
                            onUpgrade = { onUpgradeStat("STRENGTH") },
                            testTag = "upgrade_str_button"
                        )
                    }

                    item {
                        val defCost = (uiState.defenseLevel + 1) * 22L
                        StatUpgradeRow(
                            name = "Defense",
                            desc = "+3 Armor Defense",
                            level = uiState.defenseLevel,
                            cost = defCost,
                            canAfford = uiState.gold >= defCost,
                            icon = Icons.Default.Shield,
                            iconColor = Color(0xFF448AFF),
                            onUpgrade = { onUpgradeStat("DEFENSE") },
                            testTag = "upgrade_def_button"
                        )
                    }

                    item {
                        val vitCost = (uiState.vitalityLevel + 1) * 20L
                        StatUpgradeRow(
                            name = "Vitality",
                            desc = "+35 Maximum HP",
                            level = uiState.vitalityLevel,
                            cost = vitCost,
                            canAfford = uiState.gold >= vitCost,
                            icon = Icons.Default.Favorite,
                            iconColor = Color(0xFF69F0AE),
                            onUpgrade = { onUpgradeStat("VITALITY") },
                            testTag = "upgrade_vit_button"
                        )
                    }

                    item {
                        val agiCost = (uiState.agilityLevel + 1) * 45L
                        StatUpgradeRow(
                            name = "Agility",
                            desc = "+1.5% Attack Speed",
                            level = uiState.agilityLevel,
                            cost = agiCost,
                            canAfford = uiState.gold >= agiCost,
                            icon = Icons.Default.FlashOn,
                            iconColor = Color(0xFFFFD600),
                            onUpgrade = { onUpgradeStat("AGILITY") },
                            testTag = "upgrade_agi_button"
                        )
                    }

                    item {
                        val luckCost = (uiState.luckLevel + 1) * 50L
                        StatUpgradeRow(
                            name = "Luck",
                            desc = "+1% Crit Rate & +3% Crit Dmg",
                            level = uiState.luckLevel,
                            cost = luckCost,
                            canAfford = uiState.gold >= luckCost,
                            icon = Icons.Default.Star,
                            iconColor = Color(0xFFE040FB),
                            onUpgrade = { onUpgradeStat("LUCK") },
                            testTag = "upgrade_luck_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatUpgradeRow(
    name: String,
    desc: String,
    level: Int,
    cost: Long,
    canAfford: Boolean,
    icon: ImageVector,
    iconColor: Color,
    onUpgrade: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E1F2B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2D3D)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = name, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lv.$level", fontSize = 11.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Medium)
                    }
                    Text(desc, fontSize = 10.sp, color = Color.Gray)
                }
            }

            Button(
                onClick = onUpgrade,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB300),
                    disabledContainerColor = Color(0xFF2C2D3D)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag(testTag)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Gold",
                        tint = if (canAfford) Color.Black else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$cost",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (canAfford) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

