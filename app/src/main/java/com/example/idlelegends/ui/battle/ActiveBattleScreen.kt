package com.example.idlelegends.ui.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.model.CombatActionType
import com.example.idlelegends.model.CombatLog
import com.example.idlelegends.model.EnemyInstance
import com.example.idlelegends.model.EquipSlot
import com.example.idlelegends.model.EquipmentItem
import com.example.idlelegends.model.HeroClass
import com.example.idlelegends.model.HeroSkill
import com.example.idlelegends.model.WorldInfo
import com.example.idlelegends.ui.GameUiState
import com.example.idlelegends.ui.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogFilterCategory(val label: String) {
    ALL("All"),
    ATTACKS("⚔️ Attacks"),
    SKILLS("✨ Skills"),
    ENEMY("🛡️ Enemy"),
    REWARDS("🎁 Rewards")
}

@Composable
fun ActiveBattleScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    ActiveBattleContent(
        uiState = uiState,
        onCastSkill = { viewModel.castSkill(it) },
        onToggleAutoSkill = { viewModel.setAutoSkill(it) },
        onToggleAutoAdvance = { viewModel.setAutoAdvance(it) },
        onClearLogs = { viewModel.clearCombatLogs() },
        modifier = modifier
    )
}

@Composable
fun ActiveBattleContent(
    uiState: GameUiState,
    onCastSkill: (HeroSkill) -> Unit,
    onToggleAutoSkill: (Boolean) -> Unit,
    onToggleAutoAdvance: (Boolean) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(LogFilterCategory.ALL) }
    var autoScrollEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF101018))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. ACTIVE HERO SUMMARY CARD
        ActiveHeroPanel(
            heroClass = uiState.selectedHeroClass,
            playerLevel = uiState.playerLevel,
            playerXp = uiState.playerXp,
            maxXp = uiState.xpForNextLevel,
            currentHp = uiState.heroCurrentHp,
            maxHp = uiState.heroMaxHp,
            totalAttack = uiState.heroTotalAttack,
            totalDefense = uiState.heroTotalDefense,
            attackSpeed = uiState.heroAttackSpeed,
            critRate = uiState.heroCritRate,
            critMult = uiState.heroCritMultiplier,
            equipmentList = uiState.equipmentList,
            skills = uiState.activeSkills,
            onCastSkill = onCastSkill
        )

        // 2. CURRENT BATTLE PROGRESS CARD
        BattleProgressCard(
            world = uiState.currentWorld,
            currentStage = uiState.currentStage,
            currentWave = uiState.currentWave,
            currentEnemy = uiState.currentEnemy,
            autoSkill = uiState.autoSkillEnabled,
            onToggleAutoSkill = onToggleAutoSkill,
            autoAdvance = uiState.autoAdvanceEnabled,
            onToggleAutoAdvance = onToggleAutoAdvance
        )

        // 3. REAL-TIME AUTO-BATTLE ACTIONS LOG
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
}

// ==========================================
// 1. ACTIVE HERO PANEL
// ==========================================
@Composable
fun ActiveHeroPanel(
    heroClass: HeroClass,
    playerLevel: Int,
    playerXp: Long,
    maxXp: Long,
    currentHp: Long,
    maxHp: Long,
    totalAttack: Long,
    totalDefense: Long,
    attackSpeed: Float,
    critRate: Float,
    critMult: Float,
    equipmentList: List<EquipmentItem>,
    skills: List<HeroSkill>,
    onCastSkill: (HeroSkill) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF181926),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E324A)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_hero_panel")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Hero Title & Level Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = heroClass.title.take(1),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = heroClass.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF28293D)
                            ) {
                                Text(
                                    text = "Lv.$playerLevel",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = heroClass.description,
                            fontSize = 9.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // XP Tracker
                val xpProgress = (playerXp.toFloat() / maxXp.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "XP $playerXp / $maxXp",
                        fontSize = 9.sp,
                        color = Color(0xFF90CAF9),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier
                            .width(80.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF42A5F5),
                        trackColor = Color(0xFF1E2638)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hero HP Bar
            val hpPercent = (currentHp.toFloat() / maxHp.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
            val animatedHp by animateFloatAsState(targetValue = hpPercent, label = "activeHeroHp")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Health",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "HERO HP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0BEC5)
                    )
                }
                Text(
                    text = "$currentHp / $maxHp (${(hpPercent * 100).toInt()}%)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hpPercent < 0.3f) Color(0xFFFF5252) else Color.White
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { animatedHp },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (hpPercent < 0.3f) Color(0xFFFF5252) else Color(0xFF00E676),
                trackColor = Color(0xFF263238)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hero Live Combat Attributes Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HeroStatBadge(
                    label = "Attack",
                    value = "$totalAttack",
                    icon = Icons.Default.Bolt,
                    color = Color(0xFFFF5252),
                    modifier = Modifier.weight(1f)
                )
                HeroStatBadge(
                    label = "Defense",
                    value = "$totalDefense",
                    icon = Icons.Default.Shield,
                    color = Color(0xFF448AFF),
                    modifier = Modifier.weight(1f)
                )
                HeroStatBadge(
                    label = "Speed",
                    value = String.format(Locale.US, "%.2f/s", attackSpeed),
                    icon = Icons.Default.FlashOn,
                    color = Color(0xFFFFD600),
                    modifier = Modifier.weight(1f)
                )
                HeroStatBadge(
                    label = "Critical",
                    value = String.format(Locale.US, "%.0f%%", critRate * 100),
                    icon = Icons.Default.Star,
                    color = Color(0xFFE040FB),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Skills Row with CD indicators & tap to cast
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                skills.forEach { skill ->
                    val isReady = skill.currentCooldown <= 0f
                    val cdProgress = if (skill.cooldownSeconds > 0) (skill.currentCooldown / skill.cooldownSeconds).coerceIn(0f, 1f) else 0f

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable(enabled = isReady) { onCastSkill(skill) }
                            .testTag("active_skill_${skill.id}"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isReady) Color(0xFF24263B) else Color(0xFF141520),
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
        }
    }
}

@Composable
fun HeroStatBadge(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF141522),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222436)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Column {
                Text(text = label, fontSize = 8.sp, color = Color.Gray)
                Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ==========================================
// 2. CURRENT BATTLE PROGRESS CARD
// ==========================================
@Composable
fun BattleProgressCard(
    world: WorldInfo,
    currentStage: Int,
    currentWave: Int,
    currentEnemy: EnemyInstance?,
    autoSkill: Boolean,
    onToggleAutoSkill: (Boolean) -> Unit,
    autoAdvance: Boolean,
    onToggleAutoAdvance: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF181926),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E324A)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("battle_progress_card")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Stage & World Header + Automation Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFFB300).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
                        ) {
                            Text(
                                text = "W${world.id}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD54F),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = world.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFECEFF1)
                        )
                    }
                    Text(
                        text = "Stage $currentStage of 50",
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Automation quick switches
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Auto Skill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF141522))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Skills", fontSize = 10.sp, color = if (autoSkill) Color(0xFF00E5FF) else Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = autoSkill,
                            onCheckedChange = onToggleAutoSkill,
                            modifier = Modifier
                                .size(26.dp)
                                .testTag("auto_skill_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Auto Stage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF141522))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Auto Stage", fontSize = 10.sp, color = if (autoAdvance) Color(0xFF00E676) else Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = autoAdvance,
                            onCheckedChange = onToggleAutoAdvance,
                            modifier = Modifier
                                .size(26.dp)
                                .testTag("auto_stage_toggle")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5-Wave Progression Stepper Nodes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (w in 1..5) {
                    val isCompleted = w < currentWave
                    val isCurrent = w == currentWave
                    val isBossWave = w == 5

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> Color(0xFF00E676)
                                        isCurrent && isBossWave -> Color(0xFFFF1744)
                                        isCurrent -> Color(0xFFFFD54F)
                                        isBossWave -> Color(0xFF37474F)
                                        else -> Color(0xFF222436)
                                    }
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) Color.White else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Cleared",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (isBossWave) {
                                Text(
                                    text = "👑",
                                    fontSize = 12.sp
                                )
                            } else {
                                Text(
                                    text = "$w",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.Black else Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBossWave) "BOSS" else "Wave $w",
                            fontSize = 8.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) Color.White else Color.Gray
                        )
                    }

                    if (w < 5) {
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .weight(0.5f)
                                .background(if (w < currentWave) Color(0xFF00E676) else Color(0xFF2E324A))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Target Enemy Card
            if (currentEnemy != null) {
                val enemyHpPercent = (currentEnemy.currentHp.toFloat() / currentEnemy.maxHp.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
                val animatedEnemyHp by animateFloatAsState(targetValue = enemyHpPercent, label = "activeEnemyHp")

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF141520),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentEnemy.isBoss) Color(0xFFFF1744) else Color(0xFF2E324A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentEnemy.isBoss) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Boss",
                                        tint = Color(0xFFFF1744),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "BOSS: ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFF1744)
                                    )
                                }
                                Text(
                                    text = currentEnemy.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⚔️ ${currentEnemy.attack}",
                                    fontSize = 9.sp,
                                    color = Color(0xFFFF8A80),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🛡️ ${currentEnemy.defense}",
                                    fontSize = 9.sp,
                                    color = Color(0xFF90CAF9),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Enemy HP Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "HP: ${currentEnemy.currentHp} / ${currentEnemy.maxHp}",
                                fontSize = 9.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = "${(enemyHpPercent * 100).toInt()}%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentEnemy.isBoss) Color(0xFFFF5252) else Color(0xFFFFAB91)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        LinearProgressIndicator(
                            progress = { animatedEnemyHp },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (currentEnemy.isBoss) Color(0xFFFF1744) else Color(0xFFFF5252),
                            trackColor = Color(0xFF263238)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Rewards Preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Bounty: +${currentEnemy.goldReward} Gold • +${currentEnemy.xpReward} XP",
                                fontSize = 8.sp,
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentEnemy.isBoss) "★ 100% Loot Drop!" else "Loot: 28% Chance",
                                fontSize = 8.sp,
                                color = if (currentEnemy.isBoss) Color(0xFFE040FB) else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. REAL-TIME AUTO-BATTLE ACTION LOG
// ==========================================
@Composable
fun RealTimeCombatLogCard(
    combatLogs: List<CombatLog>,
    selectedFilter: LogFilterCategory,
    onSelectFilter: (LogFilterCategory) -> Unit,
    autoScrollEnabled: Boolean,
    onToggleAutoScroll: (Boolean) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Filter logs
    val filteredLogs = remember(combatLogs, selectedFilter) {
        when (selectedFilter) {
            LogFilterCategory.ALL -> combatLogs
            LogFilterCategory.ATTACKS -> combatLogs.filter {
                it.type == CombatActionType.HERO_ATTACK || it.type == CombatActionType.HERO_CRIT
            }
            LogFilterCategory.SKILLS -> combatLogs.filter {
                it.type == CombatActionType.HERO_SKILL || it.type == CombatActionType.HEAL
            }
            LogFilterCategory.ENEMY -> combatLogs.filter {
                it.type == CombatActionType.ENEMY_ATTACK || it.type == CombatActionType.HERO_DEFEATED
            }
            LogFilterCategory.REWARDS -> combatLogs.filter {
                it.type == CombatActionType.ENEMY_DEFEATED ||
                        it.type == CombatActionType.LOOT_DROP ||
                        it.type == CombatActionType.STAGE_CLEARED ||
                        it.type == CombatActionType.LEVEL_UP
            }
        }
    }

    // Auto-scroll to latest log
    LaunchedEffect(filteredLogs.size, autoScrollEnabled) {
        if (autoScrollEnabled && filteredLogs.isNotEmpty()) {
            listState.animateScrollToItem(filteredLogs.size - 1)
        }
    }

    // Infinite breathing pulse for LIVE indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF141520),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E324A)),
        modifier = modifier.testTag("combat_log_card")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Log Header with LIVE badge, Log counter, and Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Blinking Live Dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                            .alpha(liveAlpha)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REAL-TIME BATTLE LOG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E2030)
                    ) {
                        Text(
                            text = "${filteredLogs.size}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                // Controls: AutoScroll toggle and Clear
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Auto-scroll toggle chip
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (autoScrollEnabled) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color(0xFF222436),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (autoScrollEnabled) Color(0xFF00E5FF) else Color(0xFF37474F)
                        ),
                        modifier = Modifier
                            .clickable { onToggleAutoScroll(!autoScrollEnabled) }
                            .testTag("auto_scroll_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerticalAlignBottom,
                                contentDescription = "Auto Scroll",
                                tint = if (autoScrollEnabled) Color(0xFF00E5FF) else Color.Gray,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (autoScrollEnabled) "Scroll ON" else "Scroll OFF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (autoScrollEnabled) Color(0xFF00E5FF) else Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Clear logs button
                    IconButton(
                        onClick = onClearLogs,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("clear_logs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Logs",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                LogFilterCategory.values().forEach { category ->
                    val isSelected = selectedFilter == category
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) Color(0xFFFFD54F) else Color(0xFF1E2030),
                        modifier = Modifier
                            .clickable { onSelectFilter(category) }
                            .testTag("filter_${category.name.lowercase()}")
                    ) {
                        Text(
                            text = category.label,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color(0xFFB0BEC5),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Live Feed List
            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Awaiting combat actions...",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("combat_logs_list"),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(filteredLogs) { log ->
                        CombatLogItemRow(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun CombatLogItemRow(
    log: CombatLog,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeStr = remember(log.timestamp) { timeFormat.format(Date(log.timestamp)) }

    val (tagLabel, tagBg, tagTextColor) = when (log.type) {
        CombatActionType.HERO_ATTACK -> Triple("HERO", Color(0xFF1565C0).copy(alpha = 0.3f), Color(0xFF64B5F6))
        CombatActionType.HERO_CRIT -> Triple("CRIT", Color(0xFFFF8F00).copy(alpha = 0.3f), Color(0xFFFFD54F))
        CombatActionType.HERO_SKILL -> Triple("SKILL", Color(0xFF6A1B9A).copy(alpha = 0.3f), Color(0xFFE040FB))
        CombatActionType.ENEMY_ATTACK -> Triple("ENEMY", Color(0xFFC62828).copy(alpha = 0.3f), Color(0xFFFF8A80))
        CombatActionType.ENEMY_DEFEATED -> Triple("SLAIN", Color(0xFF2E7D32).copy(alpha = 0.3f), Color(0xFF00E676))
        CombatActionType.STAGE_CLEARED -> Triple("STAGE", Color(0xFFEF6C00).copy(alpha = 0.3f), Color(0xFFFFAB40))
        CombatActionType.HERO_DEFEATED -> Triple("DEATH", Color(0xFFB71C1C).copy(alpha = 0.4f), Color(0xFFFF1744))
        CombatActionType.LEVEL_UP -> Triple("LEVEL", Color(0xFFFFB300).copy(alpha = 0.4f), Color(0xFFFFE082))
        CombatActionType.LOOT_DROP -> Triple("LOOT", Color(0xFF8E24AA).copy(alpha = 0.4f), Color(0xFFEA80FC))
        CombatActionType.HEAL -> Triple("HEAL", Color(0xFF00897B).copy(alpha = 0.3f), Color(0xFF69F0AE))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0C0D14))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timestamp
        Text(
            text = timeStr,
            fontSize = 8.sp,
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Event Type Tag
        Surface(
            shape = RoundedCornerShape(3.dp),
            color = tagBg
        ) {
            Text(
                text = tagLabel,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = tagTextColor,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Log Message
        Text(
            text = log.message,
            fontSize = 9.sp,
            color = when (log.type) {
                CombatActionType.HERO_CRIT -> Color(0xFFFFD54F)
                CombatActionType.HERO_SKILL -> Color(0xFF00E5FF)
                CombatActionType.LOOT_DROP -> Color(0xFFEA80FC)
                CombatActionType.ENEMY_DEFEATED -> Color(0xFF00E676)
                CombatActionType.LEVEL_UP -> Color(0xFFFFE082)
                CombatActionType.HERO_DEFEATED -> Color(0xFFFF5252)
                CombatActionType.ENEMY_ATTACK -> Color(0xFFB0BEC5)
                else -> Color(0xFFECEFF1)
            },
            fontWeight = if (log.type == CombatActionType.HERO_CRIT || log.type == CombatActionType.LOOT_DROP || log.type == CombatActionType.LEVEL_UP) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
