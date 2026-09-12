package com.example.idlelegends.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import com.example.idlelegends.model.EquipmentItem

@Composable
fun EquipmentDetailDialog(
    item: EquipmentItem,
    playerGold: Long,
    onEquipToggle: (EquipmentItem) -> Unit,
    onUpgrade: (EquipmentItem) -> Unit,
    onDismantle: (EquipmentItem) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F2B)),
            border = BorderStroke(2.dp, item.rarity.color),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Item Name & Slot
                Text(
                    text = item.name + if (item.level > 1) " +${item.level}" else "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = item.rarity.color
                )
                Text(
                    text = "${item.rarity.title} ${item.slot.displayName}",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Stats breakdown
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF28293D),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (item.totalAttack > 0) {
                            StatRow("Attack", "+${item.totalAttack}", Color(0xFFFF5252))
                        }
                        if (item.totalDefense > 0) {
                            StatRow("Defense", "+${item.totalDefense}", Color(0xFF448AFF))
                        }
                        if (item.totalHp > 0) {
                            StatRow("Max HP", "+${item.totalHp}", Color(0xFF69F0AE))
                        }
                        if (item.critChanceBonus > 0f) {
                            StatRow("Crit Chance", "+${(item.critChanceBonus * 100).toInt()}%", Color(0xFFFFD600))
                        }
                        if (item.attackSpeedBonus > 0f) {
                            StatRow("Attack Speed", "+${(item.attackSpeedBonus * 100).toInt()}%", Color(0xFFE040FB))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Equip/Unequip, Upgrade, Dismantle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Equip / Unequip
                    Button(
                        onClick = {
                            onEquipToggle(item)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isEquipped) Color(0xFF37474F) else Color(0xFF00E676)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("equip_item_button")
                    ) {
                        Text(
                            text = if (item.isEquipped) "Unequip" else "Equip",
                            fontWeight = FontWeight.Bold,
                            color = if (item.isEquipped) Color.White else Color.Black
                        )
                    }

                    // Upgrade Button
                    val canAffordUpgrade = playerGold >= item.upgradeCostGold
                    Button(
                        onClick = { onUpgrade(item) },
                        enabled = canAffordUpgrade,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("upgrade_item_button")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Upgrade", modifier = Modifier.size(14.dp), tint = Color.Black)
                                Text("Enhance", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", modifier = Modifier.size(12.dp), tint = Color.Black)
                                Text("${item.upgradeCostGold}", fontSize = 10.sp, color = Color.Black)
                            }
                        }
                    }
                }

                if (!item.isEquipped) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            onDismantle(item)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("dismantle_item_button")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Dismantle", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dismantle (+${item.dismantleGold} Gold)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(name: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = Color.LightGray, fontSize = 13.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
