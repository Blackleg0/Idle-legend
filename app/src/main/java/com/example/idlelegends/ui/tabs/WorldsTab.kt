package com.example.idlelegends.ui.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.model.WorldCatalog

@Composable
fun WorldsTab(
    currentStage: Int,
    highestUnlockedStage: Int,
    onSelectStage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "FANTASY WORLDS (10 REALMS)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD54F),
                letterSpacing = 1.sp
            )
        }

        items(WorldCatalog.WORLDS) { world ->
            val isWorldUnlocked = highestUnlockedStage >= world.startStage
            val isCurrentWorld = currentStage in world.startStage..world.endStage

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentWorld) Color(0xFF28293D) else Color(0xFF1E1F2B)
                ),
                border = BorderStroke(
                    if (isCurrentWorld) 2.dp else 1.dp,
                    if (isCurrentWorld) Color(0xFFFFD54F) else Color(0xFF2C2D3D)
                ),
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(world.themeColorSecondary.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isWorldUnlocked) {
                                    Icon(Icons.Default.Public, contentDescription = world.name, tint = world.themeColorSecondary, modifier = Modifier.size(22.dp))
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "World ${world.id}: ${world.name}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isWorldUnlocked) Color.White else Color.Gray
                                )
                                Text(
                                    text = "Stages ${world.startStage} - ${world.endStage}",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isCurrentWorld) {
                            Surface(
                                color = Color(0xFF00E676).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "CURRENT",
                                    color = Color(0xFF00E676),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = world.description,
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // World Boss Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Boss", tint = Color(0xFFFF5252), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Boss: ${world.bossType.displayName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5252)
                        )
                    }

                    if (isWorldUnlocked) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Select Stage:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items((world.startStage..world.endStage.coerceAtMost(highestUnlockedStage)).toList()) { stg ->
                                val isSelected = stg == currentStage
                                val isBossStage = stg % 5 == 0

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        isSelected -> Color(0xFFFFB300)
                                        isBossStage -> Color(0xFF4A1212)
                                        else -> Color(0xFF28293D)
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFFFD54F) else if (isBossStage) Color(0xFFFF5252) else Color(0xFF37474F)
                                    ),
                                    modifier = Modifier
                                        .clickable { onSelectStage(stg) }
                                        .testTag("select_stage_$stg")
                                ) {
                                    Text(
                                        text = if (isBossStage) "★ $stg" else "$stg",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else if (isBossStage) Color(0xFFFF8A80) else Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
