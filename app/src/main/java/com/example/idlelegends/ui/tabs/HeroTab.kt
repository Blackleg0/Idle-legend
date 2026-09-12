package com.example.idlelegends.ui.tabs

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.model.HeroClass
import com.example.idlelegends.ui.battle.HeroSprite

@Composable
fun HeroTab(
    currentHeroClass: HeroClass,
    unlockedHeroes: Set<HeroClass>,
    playerGems: Long,
    playerLevel: Int,
    playerXp: Long,
    maxXp: Long,
    onSelectHero: (HeroClass) -> Unit,
    onUnlockHero: (HeroClass) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Player Level Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F2B)),
                border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HERO MASTERY LEVEL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "Level $playerLevel",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD54F)
                            )
                        }
                        Text(
                            text = "$playerXp / $maxXp XP",
                            fontSize = 12.sp,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    val progress = (playerXp.toFloat() / maxXp.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFF28293D)
                    )
                }
            }
        }

        // Hero Classes Header
        item {
            Text(
                text = "HERO ROSTER (5 CLASSES)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD54F),
                letterSpacing = 1.sp
            )
        }

        items(HeroClass.values()) { heroClass ->
            val isUnlocked = unlockedHeroes.contains(heroClass)
            val isCurrent = currentHeroClass == heroClass

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color(0xFF28293D) else Color(0xFF1E1F2B)
                ),
                border = BorderStroke(
                    if (isCurrent) 2.dp else 1.dp,
                    if (isCurrent) Color(0xFFFFD54F) else Color(0xFF2C2D3D)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hero avatar preview
                        HeroSprite(
                            heroClass = heroClass,
                            isAttacking = false,
                            isHit = false,
                            size = 85.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = heroClass.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = Color(0xFF00E676).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = Color(0xFF00E676),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = heroClass.description,
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                maxLines = 2,
                                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                            )

                            // Quick stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("HP ${heroClass.baseHp}", fontSize = 10.sp, color = Color(0xFF69F0AE), fontWeight = FontWeight.Bold)
                                Text("ATK ${heroClass.baseAttack}", fontSize = 10.sp, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                                Text("DEF ${heroClass.baseDefense}", fontSize = 10.sp, color = Color(0xFF448AFF), fontWeight = FontWeight.Bold)
                                Text("SPD ${heroClass.baseAttackSpeed}/s", fontSize = 10.sp, color = Color(0xFFFFD600), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons
                    if (isUnlocked) {
                        if (!isCurrent) {
                            Button(
                                onClick = { onSelectHero(heroClass) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("select_hero_${heroClass.name}")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Select", tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SELECT HERO", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        val canUnlock = playerGems >= heroClass.unlockGems
                        Button(
                            onClick = { onUnlockHero(heroClass) },
                            enabled = canUnlock,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E5FF),
                                disabledContainerColor = Color(0xFF2C2D3D)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("unlock_hero_${heroClass.name}")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Unlock", tint = if (canUnlock) Color.Black else Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UNLOCK (${heroClass.unlockGems} GEMS)",
                                color = if (canUnlock) Color.Black else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
