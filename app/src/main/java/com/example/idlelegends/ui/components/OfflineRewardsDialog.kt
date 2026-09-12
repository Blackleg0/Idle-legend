package com.example.idlelegends.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import com.example.idlelegends.model.OfflineRewardSummary

@Composable
fun OfflineRewardsDialog(
    summary: OfflineRewardSummary,
    onClaim: () -> Unit
) {
    Dialog(onDismissRequest = onClaim) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F2B)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD54F)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFD54F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom,
                        contentDescription = "Offline Progress",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "OFFLINE REWARDS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F),
                    letterSpacing = 1.sp
                )

                val hours = summary.elapsedSeconds / 3600
                val mins = (summary.elapsedSeconds % 3600) / 60
                val timeStr = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                Text(
                    text = "You were away for $timeStr. Your heroes fought bravely!",
                    fontSize = 13.sp,
                    color = Color(0xFFB0BEC5),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Rewards breakdown cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RewardItemRow(
                        icon = Icons.Default.MonetizationOn,
                        iconTint = Color(0xFFFFD54F),
                        title = "Gold Earned",
                        value = "+${summary.goldEarned}"
                    )

                    RewardItemRow(
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFF00E5FF),
                        title = "Experience",
                        value = "+${summary.xpEarned} XP"
                    )

                    RewardItemRow(
                        icon = Icons.Default.Shield,
                        iconTint = Color(0xFFFF5252),
                        title = "Monsters Defeated",
                        value = "${summary.enemiesDefeated}"
                    )
                }

                // Equipment found
                if (summary.itemsFound.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Equipment Found (${summary.itemsFound.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFECEFF1),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(summary.itemsFound) { item ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2C2D3D),
                                border = androidx.compose.foundation.BorderStroke(1.dp, item.rarity.color),
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = item.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.rarity.color
                                    )
                                    Text(
                                        text = "${item.rarity.title} ${item.slot.displayName}",
                                        fontSize = 9.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Claim Button
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("claim_offline_rewards_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Claim",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CLAIM REWARDS",
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF28293D),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = Color(0xFFCFD8DC),
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = iconTint
            )
        }
    }
}
