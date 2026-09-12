package com.example.idlelegends.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.UUID

data class DamageNumber(
    val id: String = UUID.randomUUID().toString(),
    val amount: Long,
    val isCritical: Boolean = false,
    val isHeal: Boolean = false,
    val isSkill: Boolean = false,
    val isHeroTakingDamage: Boolean = false,
    var xOffset: Float = 0f,
    var yOffset: Float = 0f,
    var alpha: Float = 1.0f,
    var createdAt: Long = System.currentTimeMillis()
)

data class Particle(
    val id: String = UUID.randomUUID().toString(),
    var position: Offset,
    var velocity: Offset,
    val color: Color,
    val size: Float,
    var alpha: Float = 1f,
    val maxAgeMs: Long = 600,
    val createdAt: Long = System.currentTimeMillis()
)

enum class CombatActionType {
    HERO_ATTACK,
    HERO_CRIT,
    HERO_SKILL,
    ENEMY_ATTACK,
    ENEMY_DEFEATED,
    STAGE_CLEARED,
    HERO_DEFEATED,
    LEVEL_UP,
    LOOT_DROP,
    HEAL
}

data class CombatLog(
    val message: String,
    val type: CombatActionType,
    val timestamp: Long = System.currentTimeMillis()
)

data class OfflineRewardSummary(
    val elapsedSeconds: Long,
    val goldEarned: Long,
    val xpEarned: Long,
    val enemiesDefeated: Int,
    val itemsFound: List<EquipmentItem>
)
