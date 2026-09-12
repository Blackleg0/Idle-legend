package com.example.idlelegends.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.idlelegends.audio.SoundManager
import com.example.idlelegends.data.local.GameDatabase
import com.example.idlelegends.data.local.GameSaveEntity
import com.example.idlelegends.data.repository.GameRepository
import com.example.idlelegends.model.CombatActionType
import com.example.idlelegends.model.CombatLog
import com.example.idlelegends.model.DamageNumber
import com.example.idlelegends.model.EnemyInstance
import com.example.idlelegends.model.EquipSlot
import com.example.idlelegends.model.EquipmentItem
import com.example.idlelegends.model.HeroClass
import com.example.idlelegends.model.HeroSkill
import com.example.idlelegends.model.ItemRarity
import com.example.idlelegends.model.OfflineRewardSummary
import com.example.idlelegends.model.SkillType
import com.example.idlelegends.model.WorldCatalog
import com.example.idlelegends.model.WorldInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val playerLevel: Int = 1,
    val playerXp: Long = 0,
    val gold: Long = 100,
    val gems: Long = 50,
    val currentStage: Int = 1,
    val currentWave: Int = 1,
    val highestUnlockedStage: Int = 1,
    val selectedHeroClass: HeroClass = HeroClass.WARRIOR,
    val unlockedHeroes: Set<HeroClass> = setOf(HeroClass.WARRIOR),
    val strengthLevel: Int = 0,
    val defenseLevel: Int = 0,
    val vitalityLevel: Int = 0,
    val agilityLevel: Int = 0,
    val luckLevel: Int = 0,
    val autoSkillEnabled: Boolean = true,
    val autoAdvanceEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,

    // Real-time battle state
    val heroCurrentHp: Long = 250,
    val heroMaxHp: Long = 250,
    val currentEnemy: EnemyInstance? = null,
    val heroAttacking: Boolean = false,
    val enemyAttacking: Boolean = false,
    val heroHit: Boolean = false,
    val enemyHit: Boolean = false,
    val damageNumbers: List<DamageNumber> = emptyList(),
    val combatLogs: List<CombatLog> = emptyList(),
    val activeSkills: List<HeroSkill> = HeroClass.WARRIOR.defaultSkills,
    val equipmentList: List<EquipmentItem> = emptyList(),

    // Dialogs
    val offlineRewardSummary: OfflineRewardSummary? = null,
    val selectedItemForDetail: EquipmentItem? = null
) {
    val currentWorld: WorldInfo get() = WorldCatalog.getWorldForStage(currentStage)
    val xpForNextLevel: Long get() = (playerLevel * playerLevel * 60L).coerceAtLeast(100L)

    val heroTotalAttack: Long get() {
        var atk = selectedHeroClass.baseAttack + strengthLevel * 4L
        equipmentList.filter { it.isEquipped }.forEach { atk += it.totalAttack }
        return atk
    }

    val heroTotalDefense: Long get() {
        var def = selectedHeroClass.baseDefense + defenseLevel * 3L
        equipmentList.filter { it.isEquipped }.forEach { def += it.totalDefense }
        return def
    }

    val heroAttackSpeed: Float get() = selectedHeroClass.baseAttackSpeed * (1f + agilityLevel * 0.015f)

    val heroCritRate: Float get() = selectedHeroClass.baseCritChance + (luckLevel * 0.01f)

    val heroCritMultiplier: Float get() = selectedHeroClass.baseCritDamage + (luckLevel * 0.03f)
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var combatLoopJob: Job? = null
    private var autoSaveJob: Job? = null

    // Combat timers (in seconds)
    private var heroAttackTimer = 0f
    private var enemyAttackTimer = 0f

    init {
        val database = GameDatabase.getInstance(application)
        repository = GameRepository(database.gameDao())

        // Load initial state and seed starter data
        viewModelScope.launch {
            val save = repository.getOrInitSave()
            val equipList = repository.equipmentFlow.first()
            val skillLevels = repository.skillLevelsFlow.first()

            val selectedHero = try {
                HeroClass.valueOf(save.selectedHeroClass)
            } catch (_: Exception) {
                HeroClass.WARRIOR
            }

            val unlocked = save.unlockedHeroes.split(",").mapNotNull {
                try { HeroClass.valueOf(it.trim()) } catch (_: Exception) { null }
            }.toSet().ifEmpty { setOf(HeroClass.WARRIOR) }

            SoundManager.soundEnabled = save.soundEnabled
            SoundManager.musicEnabled = save.musicEnabled
            if (save.musicEnabled) {
                SoundManager.startBgm()
            }

            // Apply saved skill levels to skills
            val heroSkills = selectedHero.defaultSkills.map { baseSkill ->
                val lvl = skillLevels[baseSkill.id] ?: 1
                baseSkill.copy(
                    level = lvl,
                    damageMultiplier = baseSkill.damageMultiplier * (1f + (lvl - 1) * 0.2f),
                    healPercent = (baseSkill.healPercent * (1f + (lvl - 1) * 0.15f)).coerceAtMost(0.9f),
                    cooldownSeconds = (baseSkill.cooldownSeconds * (1f - (lvl - 1) * 0.05f)).coerceAtLeast(2f)
                )
            }

            _uiState.update { current ->
                current.copy(
                    playerLevel = save.playerLevel,
                    playerXp = save.playerXp,
                    gold = save.gold,
                    gems = save.gems,
                    currentStage = save.currentStage,
                    currentWave = save.currentWave,
                    highestUnlockedStage = save.highestUnlockedStage,
                    selectedHeroClass = selectedHero,
                    unlockedHeroes = unlocked,
                    strengthLevel = save.strengthLevel,
                    defenseLevel = save.defenseLevel,
                    vitalityLevel = save.vitalityLevel,
                    agilityLevel = save.agilityLevel,
                    luckLevel = save.luckLevel,
                    autoSkillEnabled = save.autoSkillEnabled,
                    autoAdvanceEnabled = save.autoAdvanceEnabled,
                    soundEnabled = save.soundEnabled,
                    musicEnabled = save.musicEnabled,
                    activeSkills = heroSkills,
                    equipmentList = equipList
                )
            }

            recalculateHeroStats(equipList)
            spawnNextEnemy()

            // Check offline rewards
            checkOfflineRewards(save.lastSavedTimestamp)

            // Start battle loop and autosave
            startBattleLoop()
            startAutoSaveLoop()
        }
    }

    private fun checkOfflineRewards(lastSaved: Long) {
        val now = System.currentTimeMillis()
        val elapsedSec = (now - lastSaved) / 1000
        // If player was away for more than 15 seconds
        if (elapsedSec > 15) {
            val cappedSec = elapsedSec.coerceAtMost(43200) // Max 12 hours
            val stage = _uiState.value.currentStage
            val world = WorldCatalog.getWorldForStage(stage)

            // Calculate simulated offline kills
            val killsPerMin = 4 // ~15 seconds per enemy kill average
            val totalKills = ((cappedSec / 60.0) * killsPerMin).toInt().coerceAtLeast(1)

            val baseGoldPerKill = (15.0 * Math.pow(1.14, (stage - 1).toDouble())).toLong().coerceAtLeast(10)
            val baseXpPerKill = (22.0 * Math.pow(1.15, (stage - 1).toDouble())).toLong().coerceAtLeast(15)

            val goldEarned = (totalKills * baseGoldPerKill)
            val xpEarned = (totalKills * baseXpPerKill)

            // Chance to drop 1-3 equipment items
            val lootCount = (totalKills / 15).coerceIn(1, 4)
            val itemsFound = mutableListOf<EquipmentItem>()
            for (i in 0 until lootCount) {
                itemsFound.add(EquipmentItem.generateRandomLoot(stage))
            }

            _uiState.update {
                it.copy(
                    offlineRewardSummary = OfflineRewardSummary(
                        elapsedSeconds = cappedSec,
                        goldEarned = goldEarned,
                        xpEarned = xpEarned,
                        enemiesDefeated = totalKills,
                        itemsFound = itemsFound
                    )
                )
            }
        }
    }

    fun claimOfflineRewards() {
        val summary = _uiState.value.offlineRewardSummary ?: return
        SoundManager.playLevelUp()
        viewModelScope.launch {
            repository.addEquipmentList(summary.itemsFound)
            val updatedEquip = repository.equipmentFlow.first()

            _uiState.update { current ->
                val newGold = current.gold + summary.goldEarned
                current.copy(
                    gold = newGold,
                    equipmentList = updatedEquip,
                    offlineRewardSummary = null
                )
            }
            addXp(summary.xpEarned)
            saveGame()
        }
    }

    private fun recalculateHeroStats(equipList: List<EquipmentItem>) {
        val state = _uiState.value
        val hero = state.selectedHeroClass

        var bonusHp = state.vitalityLevel * 35L
        var bonusAtk = state.strengthLevel * 4L
        var bonusDef = state.defenseLevel * 3L

        equipList.filter { it.isEquipped }.forEach { eq ->
            bonusHp += eq.totalHp
            bonusAtk += eq.totalAttack
            bonusDef += eq.totalDefense
        }

        val totalMaxHp = hero.baseHp + bonusHp
        val currentHp = totalMaxHp.coerceAtMost(state.heroCurrentHp.coerceAtLeast(totalMaxHp))

        _uiState.update {
            it.copy(
                heroMaxHp = totalMaxHp,
                heroCurrentHp = currentHp
            )
        }
    }

    private fun spawnNextEnemy() {
        val state = _uiState.value
        val enemy = WorldCatalog.spawnEnemyForStage(state.currentStage, state.currentWave)
        if (enemy.isBoss) {
            SoundManager.playBossRoar()
        }
        _uiState.update { it.copy(currentEnemy = enemy) }
        enemyAttackTimer = 0f
    }

    private fun startBattleLoop() {
        combatLoopJob?.cancel()
        combatLoopJob = viewModelScope.launch {
            val tickIntervalMs = 50L
            val tickIntervalSec = tickIntervalMs / 1000f

            while (isActive) {
                delay(tickIntervalMs)
                tickCombat(tickIntervalSec)
            }
        }
    }

    private fun tickCombat(dt: Float) {
        val state = _uiState.value
        val enemy = state.currentEnemy ?: return

        // 1. Update cooldowns for hero skills
        val updatedSkills = state.activeSkills.map { skill ->
            if (skill.currentCooldown > 0f) {
                skill.copy(currentCooldown = (skill.currentCooldown - dt).coerceAtLeast(0f))
            } else skill
        }

        // 2. Auto-cast ready skills if enabled
        var newlyCastSkill: HeroSkill? = null
        if (state.autoSkillEnabled) {
            val readySkill = updatedSkills.firstOrNull { it.currentCooldown <= 0f }
            if (readySkill != null) {
                newlyCastSkill = readySkill
            }
        }

        // 3. Hero Normal Attack Timer
        val heroAtkSpeed = (state.selectedHeroClass.baseAttackSpeed * (1f + state.agilityLevel * 0.015f))
        heroAttackTimer += dt
        var heroAttacksNow = false
        if (heroAttackTimer >= (1f / heroAtkSpeed)) {
            heroAttackTimer = 0f
            heroAttacksNow = true
        }

        // 4. Enemy Attack Timer
        enemyAttackTimer += dt
        var enemyAttacksNow = false
        if (enemyAttackTimer >= (1f / enemy.attackSpeed)) {
            enemyAttackTimer = 0f
            enemyAttacksNow = true
        }

        // 5. Update floating damage numbers (rise and fade)
        val updatedDmgNumbers = state.damageNumbers.mapNotNull { dmg ->
            val age = System.currentTimeMillis() - dmg.createdAt
            if (age > 900) null
            else {
                dmg.copy(
                    yOffset = dmg.yOffset - 1.8f,
                    alpha = (1f - (age / 900f)).coerceIn(0f, 1f)
                )
            }
        }

        _uiState.update {
            it.copy(
                activeSkills = updatedSkills,
                damageNumbers = updatedDmgNumbers,
                heroAttacking = heroAttacksNow || newlyCastSkill != null,
                enemyAttacking = enemyAttacksNow,
                heroHit = enemyAttacksNow,
                enemyHit = heroAttacksNow || newlyCastSkill != null
            )
        }

        // Execute Hero Skill if ready
        if (newlyCastSkill != null) {
            castSkill(newlyCastSkill)
        }

        // Execute Hero Attack
        if (heroAttacksNow) {
            executeHeroAttack()
        }

        // Execute Enemy Attack
        if (enemyAttacksNow) {
            executeEnemyAttack()
        }
    }

    private fun executeHeroAttack() {
        val state = _uiState.value
        val enemy = state.currentEnemy ?: return

        var heroTotalAtk = state.selectedHeroClass.baseAttack + state.strengthLevel * 4L
        state.equipmentList.filter { it.isEquipped }.forEach { heroTotalAtk += it.totalAttack }

        val critRate = state.selectedHeroClass.baseCritChance + (state.luckLevel * 0.01f)
        val isCrit = Random.nextFloat() < critRate
        val critMult = state.selectedHeroClass.baseCritDamage + (state.luckLevel * 0.03f)

        var rawDmg = heroTotalAtk * (100.0 / (100.0 + enemy.defense))
        if (isCrit) {
            rawDmg *= critMult
            SoundManager.playCrit()
        } else {
            SoundManager.playAttack()
        }

        val finalDmg = rawDmg.toLong().coerceAtLeast(1L)
        val newEnemyHp = (enemy.currentHp - finalDmg).coerceAtLeast(0L)

        val dmgItem = DamageNumber(
            amount = finalDmg,
            isCritical = isCrit,
            xOffset = (220f + (Random.nextFloat() * 40f - 20f)),
            yOffset = (140f + (Random.nextFloat() * 20f - 10f))
        )

        val attackLog = if (isCrit) {
            CombatLog("CRIT! Hero struck ${enemy.name} for $finalDmg damage!", CombatActionType.HERO_CRIT)
        } else {
            CombatLog("Hero struck ${enemy.name} for $finalDmg damage", CombatActionType.HERO_ATTACK)
        }

        _uiState.update { current ->
            current.copy(
                currentEnemy = enemy.copy(currentHp = newEnemyHp),
                damageNumbers = current.damageNumbers + dmgItem,
                combatLogs = (current.combatLogs + attackLog).takeLast(80)
            )
        }

        if (newEnemyHp <= 0L) {
            handleEnemyDefeated(enemy)
        }
    }

    private fun executeEnemyAttack() {
        val state = _uiState.value
        val enemy = state.currentEnemy ?: return

        var heroDef = state.selectedHeroClass.baseDefense + state.defenseLevel * 3L
        state.equipmentList.filter { it.isEquipped }.forEach { heroDef += it.totalDefense }

        val rawDmg = enemy.attack * (100.0 / (100.0 + heroDef))
        val finalDmg = rawDmg.toLong().coerceAtLeast(1L)

        val newHeroHp = (state.heroCurrentHp - finalDmg).coerceAtLeast(0L)
        SoundManager.playHit()

        val dmgItem = DamageNumber(
            amount = finalDmg,
            isHeroTakingDamage = true,
            xOffset = (40f + (Random.nextFloat() * 30f - 15f)),
            yOffset = (150f + (Random.nextFloat() * 20f - 10f))
        )

        val enemyLog = CombatLog("${enemy.name} struck Hero for $finalDmg damage", CombatActionType.ENEMY_ATTACK)

        _uiState.update { current ->
            current.copy(
                heroCurrentHp = newHeroHp,
                damageNumbers = current.damageNumbers + dmgItem,
                combatLogs = (current.combatLogs + enemyLog).takeLast(80)
            )
        }

        if (newHeroHp <= 0L) {
            handleHeroDefeated()
        }
    }

    fun castSkill(skill: HeroSkill) {
        val state = _uiState.value
        val enemy = state.currentEnemy ?: return

        // Put on cooldown
        val updatedSkills = state.activeSkills.map {
            if (it.id == skill.id) it.copy(currentCooldown = it.cooldownSeconds) else it
        }

        SoundManager.playMagic()

        var heroTotalAtk = state.selectedHeroClass.baseAttack + state.strengthLevel * 4L
        state.equipmentList.filter { it.isEquipped }.forEach { heroTotalAtk += it.totalAttack }

        val logMessage: String

        when (skill.skillType) {
            SkillType.DAMAGE, SkillType.DAMAGE_BUFF -> {
                val skillDmg = (heroTotalAtk * skill.damageMultiplier * (100.0 / (100.0 + enemy.defense))).toLong().coerceAtLeast(5L)
                val newEnemyHp = (enemy.currentHp - skillDmg).coerceAtLeast(0L)
                val dmgItem = DamageNumber(
                    amount = skillDmg,
                    isSkill = true,
                    xOffset = 230f,
                    yOffset = 130f
                )
                logMessage = "${skill.name} dealt $skillDmg damage to ${enemy.name}!"
                _uiState.update {
                    it.copy(
                        activeSkills = updatedSkills,
                        currentEnemy = enemy.copy(currentHp = newEnemyHp),
                        damageNumbers = it.damageNumbers + dmgItem,
                        combatLogs = it.combatLogs + CombatLog(logMessage, CombatActionType.HERO_SKILL)
                    )
                }
                if (newEnemyHp <= 0L) {
                    handleEnemyDefeated(enemy)
                }
            }
            SkillType.HEAL -> {
                val healAmt = (state.heroMaxHp * skill.healPercent).toLong().coerceAtLeast(10L)
                val newHp = (state.heroCurrentHp + healAmt).coerceAtMost(state.heroMaxHp)
                val dmgItem = DamageNumber(
                    amount = healAmt,
                    isHeal = true,
                    xOffset = 50f,
                    yOffset = 130f
                )
                logMessage = "${skill.name} healed hero for +$healAmt HP!"
                _uiState.update {
                    it.copy(
                        activeSkills = updatedSkills,
                        heroCurrentHp = newHp,
                        damageNumbers = it.damageNumbers + dmgItem,
                        combatLogs = it.combatLogs + CombatLog(logMessage, CombatActionType.HERO_SKILL)
                    )
                }
            }
            SkillType.DAMAGE_HEAL -> {
                val skillDmg = (heroTotalAtk * skill.damageMultiplier * (100.0 / (100.0 + enemy.defense))).toLong().coerceAtLeast(5L)
                val newEnemyHp = (enemy.currentHp - skillDmg).coerceAtLeast(0L)
                val healAmt = (state.heroMaxHp * skill.healPercent).toLong().coerceAtLeast(10L)
                val newHp = (state.heroCurrentHp + healAmt).coerceAtMost(state.heroMaxHp)

                val dmgItem1 = DamageNumber(amount = skillDmg, isSkill = true, xOffset = 230f, yOffset = 130f)
                val dmgItem2 = DamageNumber(amount = healAmt, isHeal = true, xOffset = 50f, yOffset = 130f)
                logMessage = "${skill.name} dealt $skillDmg and healed +$healAmt HP!"

                _uiState.update {
                    it.copy(
                        activeSkills = updatedSkills,
                        heroCurrentHp = newHp,
                        currentEnemy = enemy.copy(currentHp = newEnemyHp),
                        damageNumbers = it.damageNumbers + dmgItem1 + dmgItem2,
                        combatLogs = it.combatLogs + CombatLog(logMessage, CombatActionType.HERO_SKILL)
                    )
                }
                if (newEnemyHp <= 0L) {
                    handleEnemyDefeated(enemy)
                }
            }
        }
    }

    private fun handleEnemyDefeated(enemy: EnemyInstance) {
        SoundManager.playCoin()
        val state = _uiState.value
        val goldEarned = enemy.goldReward
        val xpEarned = enemy.xpReward

        // Equipment Loot Drop Roll (25% chance for regular, 100% for boss)
        val shouldDropItem = enemy.isBoss || (Random.nextFloat() < 0.28f)
        var droppedItem: EquipmentItem? = null
        if (shouldDropItem) {
            droppedItem = EquipmentItem.generateRandomLoot(state.currentStage)
            viewModelScope.launch {
                repository.addEquipment(droppedItem)
                val updated = repository.equipmentFlow.first()
                _uiState.update { it.copy(equipmentList = updated) }
            }
        }

        // Boss gems bonus
        val gemBonus = if (enemy.isBoss) 15L else 0L

        // Stage progression
        var nextWave = state.currentWave + 1
        var nextStage = state.currentStage
        var highestStage = state.highestUnlockedStage

        if (nextWave > 5) {
            // Stage completed!
            nextWave = 1
            if (state.autoAdvanceEnabled) {
                nextStage = (state.currentStage + 1).coerceAtMost(50)
                highestStage = highestStage.coerceAtLeast(nextStage)
            }
        }

        val newLogs = mutableListOf<CombatLog>()
        val logMsg = "Defeated ${enemy.name}! Gained +$goldEarned Gold, +$xpEarned XP" +
                (if (enemy.isBoss) " (+15 Gems!)" else "")
        newLogs.add(CombatLog(logMsg, CombatActionType.ENEMY_DEFEATED))

        if (droppedItem != null) {
            newLogs.add(CombatLog("LOOT! Found [${droppedItem.rarity.name}] ${droppedItem.name} (${droppedItem.slot.displayName})!", CombatActionType.LOOT_DROP))
        }

        if (state.currentWave == 5) {
            newLogs.add(CombatLog("STAGE CLEARED! Stage ${state.currentStage} Conquered!", CombatActionType.STAGE_CLEARED))
        }

        _uiState.update { current ->
            current.copy(
                gold = current.gold + goldEarned,
                gems = current.gems + gemBonus,
                currentWave = nextWave,
                currentStage = nextStage,
                highestUnlockedStage = highestStage,
                combatLogs = (current.combatLogs + newLogs).takeLast(80)
            )
        }

        addXp(xpEarned)
        spawnNextEnemy()
    }

    private fun handleHeroDefeated() {
        val state = _uiState.value
        // Hero revives with full health and resets to wave 1
        _uiState.update {
            it.copy(
                heroCurrentHp = it.heroMaxHp,
                currentWave = 1,
                combatLogs = (it.combatLogs + CombatLog("Hero fell in battle! Revived at Wave 1.", CombatActionType.HERO_DEFEATED)).takeLast(80)
            )
        }
        spawnNextEnemy()
    }

    private fun addXp(amount: Long) {
        var currentXp = _uiState.value.playerXp + amount
        var currentLvl = _uiState.value.playerLevel
        var maxXp = _uiState.value.xpForNextLevel

        while (currentXp >= maxXp) {
            currentXp -= maxXp
            currentLvl++
            maxXp = (currentLvl * currentLvl * 60L).coerceAtLeast(100L)
            SoundManager.playLevelUp()
            _uiState.update {
                it.copy(
                    playerLevel = currentLvl,
                    playerXp = currentXp,
                    gems = it.gems + 10L, // 10 gems level up reward
                    combatLogs = (it.combatLogs + CombatLog("LEVEL UP! Hero reached Level $currentLvl! (+10 Gems)", CombatActionType.LEVEL_UP)).takeLast(80)
                )
            }
        }

        _uiState.update { it.copy(playerXp = currentXp, playerLevel = currentLvl) }
    }

    fun clearCombatLogs() {
        _uiState.update { it.copy(combatLogs = emptyList()) }
    }

    // STAT UPGRADES
    fun upgradeStat(stat: String) {
        val state = _uiState.value
        var gold = state.gold
        when (stat) {
            "STRENGTH" -> {
                val cost = (state.strengthLevel + 1) * 25L
                if (gold >= cost) {
                    SoundManager.playCoin()
                    _uiState.update { it.copy(gold = gold - cost, strengthLevel = it.strengthLevel + 1) }
                }
            }
            "DEFENSE" -> {
                val cost = (state.defenseLevel + 1) * 22L
                if (gold >= cost) {
                    SoundManager.playCoin()
                    _uiState.update { it.copy(gold = gold - cost, defenseLevel = it.defenseLevel + 1) }
                }
            }
            "VITALITY" -> {
                val cost = (state.vitalityLevel + 1) * 20L
                if (gold >= cost) {
                    SoundManager.playCoin()
                    _uiState.update { it.copy(gold = gold - cost, vitalityLevel = it.vitalityLevel + 1) }
                }
            }
            "AGILITY" -> {
                val cost = (state.agilityLevel + 1) * 45L
                if (gold >= cost) {
                    SoundManager.playCoin()
                    _uiState.update { it.copy(gold = gold - cost, agilityLevel = it.agilityLevel + 1) }
                }
            }
            "LUCK" -> {
                val cost = (state.luckLevel + 1) * 50L
                if (gold >= cost) {
                    SoundManager.playCoin()
                    _uiState.update { it.copy(gold = gold - cost, luckLevel = it.luckLevel + 1) }
                }
            }
        }
        recalculateHeroStats(_uiState.value.equipmentList)
        saveGame()
    }

    // HERO SWITCH & UNLOCK
    fun selectHero(heroClass: HeroClass) {
        if (!_uiState.value.unlockedHeroes.contains(heroClass)) return
        val newSkills = heroClass.defaultSkills
        _uiState.update {
            it.copy(
                selectedHeroClass = heroClass,
                activeSkills = newSkills
            )
        }
        recalculateHeroStats(_uiState.value.equipmentList)
        saveGame()
    }

    fun unlockHero(heroClass: HeroClass) {
        val state = _uiState.value
        if (state.gems >= heroClass.unlockGems) {
            SoundManager.playLevelUp()
            val newUnlocked = state.unlockedHeroes + heroClass
            _uiState.update {
                it.copy(
                    gems = it.gems - heroClass.unlockGems,
                    unlockedHeroes = newUnlocked,
                    selectedHeroClass = heroClass,
                    activeSkills = heroClass.defaultSkills
                )
            }
            recalculateHeroStats(_uiState.value.equipmentList)
            saveGame()
        }
    }

    // EQUIPMENT INTERACTIONS
    fun toggleEquip(item: EquipmentItem) {
        viewModelScope.launch {
            if (item.isEquipped) {
                repository.unequipItem(item)
            } else {
                repository.equipItem(item)
            }
            val updated = repository.equipmentFlow.first()
            _uiState.update { it.copy(equipmentList = updated) }
            recalculateHeroStats(updated)
            saveGame()
        }
    }

    fun upgradeEquipment(item: EquipmentItem) {
        val state = _uiState.value
        if (state.gold >= item.upgradeCostGold) {
            SoundManager.playCoin()
            val upgraded = item.copy(level = item.level + 1)
            viewModelScope.launch {
                repository.addEquipment(upgraded)
                val updated = repository.equipmentFlow.first()
                _uiState.update {
                    it.copy(
                        gold = it.gold - item.upgradeCostGold,
                        equipmentList = updated,
                        selectedItemForDetail = upgraded
                    )
                }
                recalculateHeroStats(updated)
                saveGame()
            }
        }
    }

    fun dismantleEquipment(item: EquipmentItem) {
        SoundManager.playCoin()
        viewModelScope.launch {
            repository.deleteEquipment(item.id)
            val updated = repository.equipmentFlow.first()
            _uiState.update {
                it.copy(
                    gold = it.gold + item.dismantleGold,
                    equipmentList = updated,
                    selectedItemForDetail = null
                )
            }
            recalculateHeroStats(updated)
            saveGame()
        }
    }

    fun autoEquipBest() {
        val items = _uiState.value.equipmentList
        viewModelScope.launch {
            EquipSlot.values().forEach { slot ->
                val bestItem = items
                    .filter { it.slot == slot }
                    .maxByOrNull { it.totalAttack + it.totalDefense + it.totalHp / 5 }
                if (bestItem != null && !bestItem.isEquipped) {
                    repository.equipItem(bestItem)
                }
            }
            val updated = repository.equipmentFlow.first()
            _uiState.update { it.copy(equipmentList = updated) }
            recalculateHeroStats(updated)
            SoundManager.playLevelUp()
            saveGame()
        }
    }

    // SKILL UPGRADE
    fun upgradeSkill(skill: HeroSkill) {
        val cost = (skill.level * skill.level * 120L)
        val state = _uiState.value
        if (state.gold >= cost) {
            SoundManager.playLevelUp()
            val newLvl = skill.level + 1
            viewModelScope.launch {
                repository.updateSkillLevel(skill.id, newLvl)
                val updatedSkills = state.activeSkills.map {
                    if (it.id == skill.id) {
                        it.copy(
                            level = newLvl,
                            damageMultiplier = it.damageMultiplier * 1.2f,
                            healPercent = (it.healPercent * 1.15f).coerceAtMost(0.9f),
                            cooldownSeconds = (it.cooldownSeconds * 0.95f).coerceAtLeast(2f)
                        )
                    } else it
                }
                _uiState.update {
                    it.copy(
                        gold = it.gold - cost,
                        activeSkills = updatedSkills
                    )
                }
                saveGame()
            }
        }
    }

    // STAGE SELECT
    fun selectStage(stage: Int) {
        _uiState.update {
            it.copy(
                currentStage = stage,
                currentWave = 1
            )
        }
        spawnNextEnemy()
    }

    // TOGGLES
    fun setAutoSkill(enabled: Boolean) {
        _uiState.update { it.copy(autoSkillEnabled = enabled) }
    }

    fun setAutoAdvance(enabled: Boolean) {
        _uiState.update { it.copy(autoAdvanceEnabled = enabled) }
    }

    fun toggleSound() {
        val newVal = !_uiState.value.soundEnabled
        SoundManager.soundEnabled = newVal
        _uiState.update { it.copy(soundEnabled = newVal) }
    }

    fun toggleMusic() {
        val newVal = !_uiState.value.musicEnabled
        SoundManager.musicEnabled = newVal
        if (newVal) SoundManager.startBgm() else SoundManager.stopBgm()
        _uiState.update { it.copy(musicEnabled = newVal) }
    }

    fun showItemDetail(item: EquipmentItem?) {
        _uiState.update { it.copy(selectedItemForDetail = item) }
    }

    private fun startAutoSaveLoop() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            while (isActive) {
                delay(6000L) // Autosave every 6s
                saveGame()
            }
        }
    }

    fun saveGame() {
        viewModelScope.launch {
            val s = _uiState.value
            val entity = GameSaveEntity(
                playerLevel = s.playerLevel,
                playerXp = s.playerXp,
                gold = s.gold,
                gems = s.gems,
                currentStage = s.currentStage,
                currentWave = s.currentWave,
                highestUnlockedStage = s.highestUnlockedStage,
                selectedHeroClass = s.selectedHeroClass.name,
                unlockedHeroes = s.unlockedHeroes.joinToString(",") { it.name },
                strengthLevel = s.strengthLevel,
                defenseLevel = s.defenseLevel,
                vitalityLevel = s.vitalityLevel,
                agilityLevel = s.agilityLevel,
                luckLevel = s.luckLevel,
                lastSavedTimestamp = System.currentTimeMillis(),
                autoSkillEnabled = s.autoSkillEnabled,
                autoAdvanceEnabled = s.autoAdvanceEnabled,
                soundEnabled = s.soundEnabled,
                musicEnabled = s.musicEnabled
            )
            repository.saveGameState(entity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        combatLoopJob?.cancel()
        autoSaveJob?.cancel()
        SoundManager.stopBgm()
    }
}
