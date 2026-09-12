package com.example.idlelegends.ui.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.model.HeroSkill
import com.example.idlelegends.model.SkillType

@Composable
fun SkillsTab(
    skills: List<HeroSkill>,
    playerGold: Long,
    onUpgradeSkill: (HeroSkill) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "SPECIAL HERO SKILLS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD54F),
                letterSpacing = 1.sp
            )
        }

        items(skills) { skill ->
            val upgradeCost = (skill.level * skill.level * 120L)
            val canAfford = playerGold >= upgradeCost

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F2B)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0x3300E5FF), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = "Skill", tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = skill.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Level ${skill.level}",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF28293D)
                        ) {
                            Text(
                                text = "${skill.cooldownSeconds}s CD",
                                fontSize = 11.sp,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = skill.description,
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statText = when (skill.skillType) {
                            SkillType.DAMAGE -> "Damage: ${(skill.damageMultiplier * 100).toInt()}%"
                            SkillType.HEAL -> "Heal: ${(skill.healPercent * 100).toInt()}% HP"
                            SkillType.DAMAGE_BUFF -> "Damage: ${(skill.damageMultiplier * 100).toInt()}% + DEF"
                            SkillType.DAMAGE_HEAL -> "Damage: ${(skill.damageMultiplier * 100).toInt()}% + Heal ${(skill.healPercent * 100).toInt()}%"
                        }
                        Text(
                            text = statText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676)
                        )

                        Button(
                            onClick = { onUpgradeSkill(skill) },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300),
                                disabledContainerColor = Color(0xFF2C2D3D)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("upgrade_skill_${skill.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Upgrade", modifier = Modifier.size(14.dp), tint = if (canAfford) Color.Black else Color.Gray)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", modifier = Modifier.size(14.dp), tint = if (canAfford) Color.Black else Color.Gray)
                                Text(
                                    text = "$upgradeCost",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (canAfford) Color.Black else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
