package com.example.idlelegends.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class EquipSlot(val displayName: String) {
    WEAPON("Weapon"),
    ARMOR("Armor"),
    HELMET("Helmet"),
    BOOTS("Boots"),
    ACCESSORY("Accessory")
}

enum class ItemRarity(
    val title: String,
    val color: Color,
    val statMultiplier: Float,
    val dropWeight: Int
) {
    COMMON("Common", Color(0xFFB0B0B0), 1.0f, 60),
    UNCOMMON("Uncommon", Color(0xFF4CAF50), 1.35f, 25),
    RARE("Rare", Color(0xFF2196F3), 1.85f, 10),
    EPIC("Epic", Color(0xFF9C27B0), 2.6f, 4),
    LEGENDARY("Legendary", Color(0xFFFF9800), 3.8f, 1),
    MYTHIC("Mythic", Color(0xFFE91E63), 5.5f, 0); // Special boss drop or high world drop

    companion object {
        fun rollRarity(stage: Int): ItemRarity {
            val roll = (1..100).random()
            val bonus = (stage / 5).coerceAtMost(25)
            val adjustedRoll = roll - bonus
            return when {
                adjustedRoll <= 2 && stage >= 20 -> MYTHIC
                adjustedRoll <= 7 && stage >= 10 -> LEGENDARY
                adjustedRoll <= 18 -> EPIC
                adjustedRoll <= 40 -> RARE
                adjustedRoll <= 70 -> UNCOMMON
                else -> COMMON
            }
        }
    }
}

data class EquipmentItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val slot: EquipSlot,
    val rarity: ItemRarity,
    val level: Int = 1,
    val baseAttack: Long = 0,
    val baseDefense: Long = 0,
    val baseHp: Long = 0,
    val critChanceBonus: Float = 0f,
    val attackSpeedBonus: Float = 0f,
    val isEquipped: Boolean = false
) {
    // Current stats after enhancements
    val totalAttack: Long get() = (baseAttack * (1.0 + (level - 1) * 0.15)).toLong()
    val totalDefense: Long get() = (baseDefense * (1.0 + (level - 1) * 0.15)).toLong()
    val totalHp: Long get() = (baseHp * (1.0 + (level - 1) * 0.15)).toLong()

    val upgradeCostGold: Long get() = (level * level * 80L * rarity.statMultiplier).toLong()
    val dismantleGold: Long get() = (level * 35L * rarity.statMultiplier).toLong()

    companion object {
        private val WEAPON_NAMES = mapOf(
            ItemRarity.COMMON to listOf("Rusty Blade", "Wooden Bow", "Novice Wand", "Iron Dagger", "Wooden Mace"),
            ItemRarity.UNCOMMON to listOf("Steel Broadsword", "Composite Bow", "Apprentice Staff", "Stiletto", "Morning Star"),
            ItemRarity.RARE to listOf("Gladiator Cleaver", "Ranger's Longbow", "Ember Wand", "Shadow Fang", "Templar Warhammer"),
            ItemRarity.EPIC to listOf("Dragonfang Greatsword", "Windrunner Bow", "Nether Void Staff", "Nightfall Kris", "Radiant Scepter"),
            ItemRarity.LEGENDARY to listOf("Excalibur", "Starfall Artemis", "Archmage Omnistaff", "Deathwhisper Edge", "Aegis Sunbreaker"),
            ItemRarity.MYTHIC to listOf("Godslayer Blade", "Celestial Void Bow", "Infinite Singularity", "Bloodmoon Reaper", "Genesis Mjolnir")
        )

        private val ARMOR_NAMES = mapOf(
            ItemRarity.COMMON to listOf("Tattered Tunic", "Cloth Robe", "Leather Vest"),
            ItemRarity.UNCOMMON to listOf("Chainmail Hauberk", "Reinforced Jerkin", "Silk Vestments"),
            ItemRarity.RARE to listOf("Knight's Cuirass", "Shadowscale Armor", "Runic Raiment"),
            ItemRarity.EPIC to listOf("Dragonscale Plate", "Phantom Garb", "Archon Vestments"),
            ItemRarity.LEGENDARY to listOf("Aegis Immortal Plate", "Wraith Void Armor", "Astral Robes"),
            ItemRarity.MYTHIC to listOf("Primordial Titan Carapace", "Void Sovereign Armor", "Celestial Vestments")
        )

        private val HELMET_NAMES = mapOf(
            ItemRarity.COMMON to listOf("Iron Cap", "Cloth Hood", "Leather Cowl"),
            ItemRarity.UNCOMMON to listOf("Steel Helmet", "Hunter's Hood", "Scholar's Circlet"),
            ItemRarity.RARE to listOf("Barbarian Mask", "Ranger Goggles", "Mystic Diadem"),
            ItemRarity.EPIC to listOf("Obsidian Crown", "Assassin Shroud", "Astral Tiara"),
            ItemRarity.LEGENDARY to listOf("Crown of the Colossus", "Eclipse Mask", "Halo of Seraphim"),
            ItemRarity.MYTHIC to listOf("Infinity Visor", "Eye of the Void", "Crown of Eternity")
        )

        private val BOOTS_NAMES = mapOf(
            ItemRarity.COMMON to listOf("Worn Sandals", "Rough Boots"),
            ItemRarity.UNCOMMON to listOf("Leather Treads", "Padded Greaves"),
            ItemRarity.RARE to listOf("Windstride Boots", "Armored Sabatons"),
            ItemRarity.EPIC to listOf("Pegasus Greaves", "Shadow Stalker Treads"),
            ItemRarity.LEGENDARY to listOf("Hermes Winged Boots", "Void Stride Greaves"),
            ItemRarity.MYTHIC to listOf("Chronos Warp Treads", "Divine Path Greaves")
        )

        private val ACCESSORY_NAMES = mapOf(
            ItemRarity.COMMON to listOf("Copper Ring", "Bead Amulet"),
            ItemRarity.UNCOMMON to listOf("Silver Band", "Jade Pendant"),
            ItemRarity.RARE to listOf("Ruby Ring of Vigor", "Sapphire Choker"),
            ItemRarity.EPIC to listOf("Ring of Pure Fire", "Heart of the Mountain"),
            ItemRarity.LEGENDARY to listOf("Band of the Phoenix", "Eye of the Storm"),
            ItemRarity.MYTHIC to listOf("Omega Ring of Eternity", "Heart of Creation")
        )

        fun generateRandomLoot(stage: Int, forcedRarity: ItemRarity? = null): EquipmentItem {
            val rarity = forcedRarity ?: ItemRarity.rollRarity(stage)
            val slot = EquipSlot.values().random()
            val nameList = when (slot) {
                EquipSlot.WEAPON -> WEAPON_NAMES[rarity] ?: WEAPON_NAMES[ItemRarity.COMMON]!!
                EquipSlot.ARMOR -> ARMOR_NAMES[rarity] ?: ARMOR_NAMES[ItemRarity.COMMON]!!
                EquipSlot.HELMET -> HELMET_NAMES[rarity] ?: HELMET_NAMES[ItemRarity.COMMON]!!
                EquipSlot.BOOTS -> BOOTS_NAMES[rarity] ?: BOOTS_NAMES[ItemRarity.COMMON]!!
                EquipSlot.ACCESSORY -> ACCESSORY_NAMES[rarity] ?: ACCESSORY_NAMES[ItemRarity.COMMON]!!
            }
            val name = nameList.random()

            // Scale stats with stage and rarity
            val mult = rarity.statMultiplier * (1f + (stage - 1) * 0.12f)
            var atk = 0L
            var def = 0L
            var hp = 0L
            var critRate = 0f
            var spd = 0f

            when (slot) {
                EquipSlot.WEAPON -> {
                    atk = (15 * mult).toLong().coerceAtLeast(5)
                    if (rarity >= ItemRarity.RARE) critRate = (0.02f * rarity.ordinal).coerceAtMost(0.15f)
                }
                EquipSlot.ARMOR -> {
                    def = (12 * mult).toLong().coerceAtLeast(3)
                    hp = (50 * mult).toLong().coerceAtLeast(20)
                }
                EquipSlot.HELMET -> {
                    def = (8 * mult).toLong().coerceAtLeast(2)
                    hp = (35 * mult).toLong().coerceAtLeast(15)
                }
                EquipSlot.BOOTS -> {
                    def = (6 * mult).toLong().coerceAtLeast(2)
                    if (rarity >= ItemRarity.UNCOMMON) spd = (0.03f * rarity.ordinal).coerceAtMost(0.15f)
                }
                EquipSlot.ACCESSORY -> {
                    atk = (8 * mult).toLong().coerceAtLeast(3)
                    hp = (30 * mult).toLong().coerceAtLeast(10)
                    if (rarity >= ItemRarity.RARE) critRate = 0.04f
                }
            }

            return EquipmentItem(
                name = name,
                slot = slot,
                rarity = rarity,
                level = 1,
                baseAttack = atk,
                baseDefense = def,
                baseHp = hp,
                critChanceBonus = critRate,
                attackSpeedBonus = spd,
                isEquipped = false
            )
        }
    }
}
