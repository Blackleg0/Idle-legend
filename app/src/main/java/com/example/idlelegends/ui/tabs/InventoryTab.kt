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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idlelegends.model.EquipSlot
import com.example.idlelegends.model.EquipmentItem

@Composable
fun InventoryTab(
    equipmentList: List<EquipmentItem>,
    onItemClick: (EquipmentItem) -> Unit,
    onAutoEquipBest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<EquipSlot?>(null) }

    val equippedMap = remember(equipmentList) {
        EquipSlot.values().associateWith { slot ->
            equipmentList.find { it.isEquipped && it.slot == slot }
        }
    }

    val unequippedList = remember(equipmentList, selectedFilter) {
        equipmentList.filter { !it.isEquipped && (selectedFilter == null || it.slot == selectedFilter) }
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Equipped Gear Header & Auto Equip button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EQUIPPED GEAR",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F),
                    letterSpacing = 1.sp
                )

                Button(
                    onClick = onAutoEquipBest,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("auto_equip_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Auto Equip", tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto Equip Best", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5 Equipped Slots Display
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EquipSlot.values().forEach { slot ->
                    val item = equippedMap[slot]
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E1F2B),
                        border = BorderStroke(1.dp, item?.rarity?.color ?: Color(0xFF37474F)),
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clickable(enabled = item != null) {
                                item?.let { onItemClick(it) }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = slot.displayName,
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            if (item != null) {
                                Text(
                                    text = item.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = item.rarity.color,
                                    maxLines = 1
                                )
                                Text(
                                    text = "+${item.level}",
                                    fontSize = 9.sp,
                                    color = Color(0xFFFFD54F)
                                )
                            } else {
                                Text(
                                    text = "Empty",
                                    fontSize = 10.sp,
                                    color = Color(0xFF455A64)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter chips row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BAG (${unequippedList.size} ITEMS)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F),
                    letterSpacing = 1.sp
                )
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFD54F),
                            selectedLabelColor = Color.Black
                        )
                    )
                }
                items(EquipSlot.values()) { slot ->
                    FilterChip(
                        selected = selectedFilter == slot,
                        onClick = { selectedFilter = if (selectedFilter == slot) null else slot },
                        label = { Text(slot.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFD54F),
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        // Inventory Items Grid/List
        if (unequippedList.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E1F2B),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No items in bag", color = Color.Gray, fontSize = 14.sp)
                        Text("Fight monsters and defeat bosses to collect rare equipment!", color = Color.DarkGray, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(unequippedList) { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1F2B),
                    border = BorderStroke(1.dp, item.rarity.color),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .testTag("inventory_item_${item.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name + if (item.level > 1) " +${item.level}" else "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = item.rarity.color
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = item.rarity.color.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = item.rarity.title,
                                        color = item.rarity.color,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${item.slot.displayName} • " + when {
                                    item.totalAttack > 0 -> "ATK +${item.totalAttack}"
                                    item.totalDefense > 0 -> "DEF +${item.totalDefense}"
                                    else -> "HP +${item.totalHp}"
                                },
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }

                        Button(
                            onClick = { onItemClick(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28293D)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Manage", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
