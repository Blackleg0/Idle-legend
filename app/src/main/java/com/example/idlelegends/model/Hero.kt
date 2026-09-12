package com.example.idlelegends.model

enum class HeroClass(
    val title: String,
    val description: String,
    val baseHp: Long,
    val baseAttack: Long,
    val baseDefense: Long,
    val baseAttackSpeed: Float, // Attacks per second
    val baseCritChance: Float,  // 0.05 = 5%
    val baseCritDamage: Float,  // 1.5 = 150%
    val unlockGems: Long
) {
    WARRIOR(
        title = "Warrior",
        description = "Mighty frontline fighter with impenetrable armor and high health.",
        baseHp = 250,
        baseAttack = 28,
        baseDefense = 18,
        baseAttackSpeed = 1.0f,
        baseCritChance = 0.08f,
        baseCritDamage = 1.6f,
        unlockGems = 0 // Default unlocked
    ),
    MAGE(
        title = "Mage",
        description = "Master of arcane magic wielding devastating area burst attacks.",
        baseHp = 160,
        baseAttack = 46,
        baseDefense = 8,
        baseAttackSpeed = 0.85f,
        baseCritChance = 0.12f,
        baseCritDamage = 2.0f,
        unlockGems = 100
    ),
    ARCHER(
        title = "Archer",
        description = "Swift sharpshooter striking relentlessly from afar with rapid volleys.",
        baseHp = 190,
        baseAttack = 34,
        baseDefense = 11,
        baseAttackSpeed = 1.45f,
        baseCritChance = 0.18f,
        baseCritDamage = 1.75f,
        unlockGems = 200
    ),
    ASSASSIN(
        title = "Assassin",
        description = "Shadow stalker inflicting lethal critical strikes with uncanny speed.",
        baseHp = 175,
        baseAttack = 40,
        baseDefense = 9,
        baseAttackSpeed = 1.35f,
        baseCritChance = 0.28f,
        baseCritDamage = 2.4f,
        unlockGems = 350
    ),
    PALADIN(
        title = "Paladin",
        description = "Holy champion balancing righteous radiant hammer strikes with divine healing.",
        baseHp = 300,
        baseAttack = 25,
        baseDefense = 22,
        baseAttackSpeed = 0.95f,
        baseCritChance = 0.06f,
        baseCritDamage = 1.5f,
        unlockGems = 500
    );

    val defaultSkills: List<HeroSkill>
        get() = when (this) {
            WARRIOR -> listOf(
                HeroSkill(
                    id = "w_slash",
                    name = "Whirlwind Slash",
                    description = "Spin attack dealing 250% physical damage",
                    cooldownSeconds = 6f,
                    damageMultiplier = 2.5f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "w_shield",
                    name = "Shield Slam",
                    description = "Heavy shield blow dealing 180% damage and boosting defense",
                    cooldownSeconds = 9f,
                    damageMultiplier = 1.8f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE_BUFF,
                    level = 1
                ),
                HeroSkill(
                    id = "w_fortress",
                    name = "Iron Fortress",
                    description = "Unleash battle shout recovering 20% HP",
                    cooldownSeconds = 15f,
                    damageMultiplier = 0f,
                    healPercent = 0.20f,
                    skillType = SkillType.HEAL,
                    level = 1
                )
            )
            MAGE -> listOf(
                HeroSkill(
                    id = "m_fireball",
                    name = "Arcane Fireball",
                    description = "Blazing orb of fire dealing 320% magic damage",
                    cooldownSeconds = 5f,
                    damageMultiplier = 3.2f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "m_nova",
                    name = "Frost Nova",
                    description = "Explosive frost dealing 220% magic damage",
                    cooldownSeconds = 8f,
                    damageMultiplier = 2.2f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "m_meteor",
                    name = "Cataclysm Meteor",
                    description = "Summon massive apocalyptic meteor dealing 480% damage",
                    cooldownSeconds = 16f,
                    damageMultiplier = 4.8f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                )
            )
            ARCHER -> listOf(
                HeroSkill(
                    id = "a_pierce",
                    name = "Piercing Arrow",
                    description = "High velocity arrow dealing 260% physical damage",
                    cooldownSeconds = 4f,
                    damageMultiplier = 2.6f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "a_volley",
                    name = "Arrow Volley",
                    description = "Rapid burst of 5 arrows dealing 300% total damage",
                    cooldownSeconds = 7f,
                    damageMultiplier = 3.0f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "a_rain",
                    name = "Skyfall Rain",
                    description = "Blot out the sun with arrows dealing 420% damage",
                    cooldownSeconds = 14f,
                    damageMultiplier = 4.2f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                )
            )
            ASSASSIN -> listOf(
                HeroSkill(
                    id = "as_shadow",
                    name = "Shadow Step",
                    description = "Slip into darkness and strike for 350% critical damage",
                    cooldownSeconds = 5f,
                    damageMultiplier = 3.5f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "as_daggers",
                    name = "Dancing Daggers",
                    description = "Whirl of blades dealing 280% damage",
                    cooldownSeconds = 8f,
                    damageMultiplier = 2.8f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "as_execute",
                    name = "Fatal Strike",
                    description = "Lethal finishing strike dealing 550% massive damage",
                    cooldownSeconds = 15f,
                    damageMultiplier = 5.5f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                )
            )
            PALADIN -> listOf(
                HeroSkill(
                    id = "p_smite",
                    name = "Holy Smite",
                    description = "Consecrated hammer dealing 220% radiant damage",
                    cooldownSeconds = 5f,
                    damageMultiplier = 2.2f,
                    healPercent = 0f,
                    skillType = SkillType.DAMAGE,
                    level = 1
                ),
                HeroSkill(
                    id = "p_divine",
                    name = "Divine Light",
                    description = "Call upon heavenly grace to heal 35% of max HP",
                    cooldownSeconds = 12f,
                    damageMultiplier = 0f,
                    healPercent = 0.35f,
                    skillType = SkillType.HEAL,
                    level = 1
                ),
                HeroSkill(
                    id = "p_judgement",
                    name = "Heaven's Wrath",
                    description = "Smite evil dealing 380% holy damage and healing 15% HP",
                    cooldownSeconds = 18f,
                    damageMultiplier = 3.8f,
                    healPercent = 0.15f,
                    skillType = SkillType.DAMAGE_HEAL,
                    level = 1
                )
            )
        }
}

enum class SkillType {
    DAMAGE,
    HEAL,
    DAMAGE_BUFF,
    DAMAGE_HEAL
}

data class HeroSkill(
    val id: String,
    val name: String,
    val description: String,
    val cooldownSeconds: Float,
    val damageMultiplier: Float,
    val healPercent: Float,
    val skillType: SkillType,
    val level: Int = 1,
    var currentCooldown: Float = 0f
)
