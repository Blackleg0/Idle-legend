package com.example.idlelegends.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.idlelegends.model.EquipSlot
import com.example.idlelegends.model.EquipmentItem
import com.example.idlelegends.model.ItemRarity

@Entity(tableName = "game_save")
data class GameSaveEntity(
    @PrimaryKey val id: Int = 1,
    val playerLevel: Int = 1,
    val playerXp: Long = 0,
    val gold: Long = 100,
    val gems: Long = 50,
    val currentStage: Int = 1,
    val currentWave: Int = 1,
    val highestUnlockedStage: Int = 1,
    val selectedHeroClass: String = "WARRIOR",
    val unlockedHeroes: String = "WARRIOR",
    val strengthLevel: Int = 0,
    val defenseLevel: Int = 0,
    val vitalityLevel: Int = 0,
    val agilityLevel: Int = 0,
    val luckLevel: Int = 0,
    val lastSavedTimestamp: Long = System.currentTimeMillis(),
    val autoSkillEnabled: Boolean = true,
    val autoAdvanceEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true
)

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slot: String,
    val rarity: String,
    val level: Int,
    val baseAttack: Long,
    val baseDefense: Long,
    val baseHp: Long,
    val critChanceBonus: Float,
    val attackSpeedBonus: Float,
    val isEquipped: Boolean
) {
    fun toDomain(): EquipmentItem {
        return EquipmentItem(
            id = id,
            name = name,
            slot = EquipSlot.valueOf(slot),
            rarity = ItemRarity.valueOf(rarity),
            level = level,
            baseAttack = baseAttack,
            baseDefense = baseDefense,
            baseHp = baseHp,
            critChanceBonus = critChanceBonus,
            attackSpeedBonus = attackSpeedBonus,
            isEquipped = isEquipped
        )
    }

    companion object {
        fun fromDomain(item: EquipmentItem): EquipmentEntity {
            return EquipmentEntity(
                id = item.id,
                name = item.name,
                slot = item.slot.name,
                rarity = item.rarity.name,
                level = item.level,
                baseAttack = item.baseAttack,
                baseDefense = item.baseDefense,
                baseHp = item.baseHp,
                critChanceBonus = item.critChanceBonus,
                attackSpeedBonus = item.attackSpeedBonus,
                isEquipped = item.isEquipped
            )
        }
    }
}

@Entity(tableName = "skill_levels")
data class SkillLevelEntity(
    @PrimaryKey val skillId: String,
    val level: Int
)
