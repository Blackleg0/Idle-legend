package com.example.idlelegends.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_save WHERE id = 1")
    fun getGameSaveFlow(): Flow<GameSaveEntity?>

    @Query("SELECT * FROM game_save WHERE id = 1")
    suspend fun getGameSave(): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameState(state: GameSaveEntity)

    // Equipment
    @Query("SELECT * FROM equipment")
    fun getAllEquipmentFlow(): Flow<List<EquipmentEntity>>

    @Query("SELECT * FROM equipment")
    suspend fun getAllEquipment(): List<EquipmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(items: List<EquipmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleEquipment(item: EquipmentEntity)

    @Query("DELETE FROM equipment WHERE id = :itemId")
    suspend fun deleteEquipment(itemId: String)

    @Query("UPDATE equipment SET isEquipped = 0 WHERE slot = :slot")
    suspend fun unequipSlot(slot: String)

    // Skill levels
    @Query("SELECT * FROM skill_levels")
    fun getAllSkillLevelsFlow(): Flow<List<SkillLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkillLevel(skill: SkillLevelEntity)
}
