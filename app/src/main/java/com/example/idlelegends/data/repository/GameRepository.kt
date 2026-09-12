package com.example.idlelegends.data.repository

import com.example.idlelegends.data.local.EquipmentEntity
import com.example.idlelegends.data.local.GameDao
import com.example.idlelegends.data.local.GameSaveEntity
import com.example.idlelegends.data.local.SkillLevelEntity
import com.example.idlelegends.model.EquipSlot
import com.example.idlelegends.model.EquipmentItem
import com.example.idlelegends.model.ItemRarity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    val gameSaveFlow: Flow<GameSaveEntity?> = gameDao.getGameSaveFlow()

    val equipmentFlow: Flow<List<EquipmentItem>> = gameDao.getAllEquipmentFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    val skillLevelsFlow: Flow<Map<String, Int>> = gameDao.getAllSkillLevelsFlow().map { list ->
        list.associate { it.skillId to it.level }
    }

    suspend fun getOrInitSave(): GameSaveEntity = withContext(Dispatchers.IO) {
        val existing = gameDao.getGameSave()
        if (existing != null) {
            existing
        } else {
            val initial = GameSaveEntity()
            gameDao.saveGameState(initial)

            // Seed initial starter starter weapon and armor
            val starterWeapon = EquipmentItem(
                name = "Apprentice Broadsword",
                slot = EquipSlot.WEAPON,
                rarity = ItemRarity.COMMON,
                level = 1,
                baseAttack = 15,
                isEquipped = true
            )
            val starterArmor = EquipmentItem(
                name = "Traveler Tunic",
                slot = EquipSlot.ARMOR,
                rarity = ItemRarity.COMMON,
                level = 1,
                baseDefense = 10,
                baseHp = 40,
                isEquipped = true
            )
            gameDao.insertEquipment(listOf(
                EquipmentEntity.fromDomain(starterWeapon),
                EquipmentEntity.fromDomain(starterArmor)
            ))

            initial
        }
    }

    suspend fun saveGameState(state: GameSaveEntity) = withContext(Dispatchers.IO) {
        gameDao.saveGameState(state)
    }

    suspend fun addEquipment(item: EquipmentItem) = withContext(Dispatchers.IO) {
        gameDao.insertSingleEquipment(EquipmentEntity.fromDomain(item))
    }

    suspend fun addEquipmentList(items: List<EquipmentItem>) = withContext(Dispatchers.IO) {
        gameDao.insertEquipment(items.map { EquipmentEntity.fromDomain(it) })
    }

    suspend fun equipItem(item: EquipmentItem) = withContext(Dispatchers.IO) {
        // First unequip current item in this slot
        gameDao.unequipSlot(item.slot.name)
        // Equip this item
        gameDao.insertSingleEquipment(EquipmentEntity.fromDomain(item.copy(isEquipped = true)))
    }

    suspend fun unequipItem(item: EquipmentItem) = withContext(Dispatchers.IO) {
        gameDao.insertSingleEquipment(EquipmentEntity.fromDomain(item.copy(isEquipped = false)))
    }

    suspend fun deleteEquipment(itemId: String) = withContext(Dispatchers.IO) {
        gameDao.deleteEquipment(itemId)
    }

    suspend fun updateSkillLevel(skillId: String, level: Int) = withContext(Dispatchers.IO) {
        gameDao.insertSkillLevel(SkillLevelEntity(skillId, level))
    }
}
