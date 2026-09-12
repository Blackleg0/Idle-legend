package com.example.idlelegends.ui.battle

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.model.DamageNumber
import com.example.idlelegends.model.EnemyInstance
import com.example.idlelegends.model.HeroClass
import com.example.idlelegends.model.WorldInfo
import kotlin.math.roundToInt

@Composable
fun BattlefieldView(
    world: WorldInfo,
    stage: Int,
    wave: Int,
    heroClass: HeroClass,
    heroLevel: Int,
    heroHp: Long,
    heroMaxHp: Long,
    enemy: EnemyInstance?,
    heroAttacking: Boolean,
    enemyAttacking: Boolean,
    heroHit: Boolean,
    enemyHit: Boolean,
    damageNumbers: List<DamageNumber>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(2.dp, Color(0xFF37474F), RoundedCornerShape(18.dp))
    ) {
        // Dynamic procedural world background
        WorldBackground(world = world)

        // Stage Header Banner
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD121212),
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (enemy?.isBoss == true) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Boss Alert",
                        tint = Color(0xFFFF1744),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BOSS BATTLE - STAGE $stage",
                        color = Color(0xFFFF1744),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = "W${world.id}: ${world.name} • Stage $stage (Wave $wave/5)",
                        color = Color(0xFFECEFF1),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Combatants Row: Hero on left, Enemy on right
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // HERO SECTION
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(135.dp)
            ) {
                // Hero HP Bar
                val heroHpPercent = (heroHp.toFloat() / heroMaxHp.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
                val animatedHeroHp by animateFloatAsState(targetValue = heroHpPercent, label = "heroHp")

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xAA000000),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Lv.$heroLevel ${heroClass.title}",
                                color = Color(0xFFFFD54F),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$heroHp/$heroMaxHp",
                                color = Color.White,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { animatedHeroHp },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF00E676),
                            trackColor = Color(0xFF37474F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Hero Sprite
                HeroSprite(
                    heroClass = heroClass,
                    isAttacking = heroAttacking,
                    isHit = heroHit,
                    size = 130.dp,
                    modifier = Modifier.testTag("hero_sprite")
                )
            }

            // ENEMY SECTION
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(145.dp)
            ) {
                if (enemy != null) {
                    // Enemy HP Bar
                    val enemyHpPercent = (enemy.currentHp.toFloat() / enemy.maxHp.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
                    val animatedEnemyHp by animateFloatAsState(targetValue = enemyHpPercent, label = "enemyHp")

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xAA000000),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = enemy.name,
                                    color = if (enemy.isBoss) Color(0xFFFF5252) else Color(0xFFECEFF1),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${enemy.currentHp}",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 9.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = { animatedEnemyHp },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (enemy.isBoss) Color(0xFFFF1744) else Color(0xFFFF5252),
                                trackColor = Color(0xFF37474F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Monster Sprite
                    MonsterSprite(
                        type = enemy.type,
                        isAttacking = enemyAttacking,
                        isHit = enemyHit,
                        size = if (enemy.isBoss) 150.dp else 125.dp,
                        modifier = Modifier.testTag("monster_sprite")
                    )
                }
            }
        }

        // Floating combat damage numbers
        damageNumbers.forEach { dmg ->
            val textColor = when {
                dmg.isHeal -> Color(0xFF00E676)
                dmg.isCritical -> Color(0xFFFFD600)
                dmg.isSkill -> Color(0xFF00E5FF)
                dmg.isHeroTakingDamage -> Color(0xFFFF5252)
                else -> Color.White
            }
            val textPrefix = when {
                dmg.isHeal -> "+ "
                dmg.isCritical -> "CRIT! "
                else -> ""
            }
            val fontSize = if (dmg.isCritical || dmg.isSkill) 16.sp else 13.sp

            Text(
                text = "$textPrefix${dmg.amount}",
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .offset { IntOffset(dmg.xOffset.roundToInt(), dmg.yOffset.roundToInt()) }
                    .alpha(dmg.alpha)
            )
        }
    }
}
