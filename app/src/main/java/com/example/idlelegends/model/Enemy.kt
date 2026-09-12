package com.example.idlelegends.model

import androidx.compose.ui.graphics.Color

enum class MonsterType(
    val displayName: String,
    val isBoss: Boolean,
    val primaryColor: Color,
    val secondaryColor: Color,
    val hpScale: Float,
    val atkScale: Float,
    val defScale: Float
) {
    SLIME("Forest Slime", false, Color(0xFF4CAF50), Color(0xFF81C784), 0.9f, 0.8f, 0.5f),
    WOLF("Shadow Wolf", false, Color(0xFF5D6D7E), Color(0xFF34495E), 1.0f, 1.1f, 0.8f),
    GOBLIN("Goblin Scout", false, Color(0xFF689F38), Color(0xFF8BC34A), 1.1f, 1.0f, 0.9f),
    SKELETON("Bone Warrior", false, Color(0xFFECEFF1), Color(0xFF90A4AE), 1.15f, 1.15f, 1.0f),
    ORC("Orc Berserker", false, Color(0xFF795548), Color(0xFF4E342E), 1.4f, 1.25f, 1.2f),
    UNDEAD_WARRIOR("Crypt Revenant", false, Color(0xFF37474F), Color(0xFF78909C), 1.35f, 1.2f, 1.3f),
    DARK_MAGE("Necromancer", false, Color(0xFF6A1B9A), Color(0xFFAB47BC), 1.1f, 1.45f, 0.7f),
    GARGOYLE("Stone Gargoyle", false, Color(0xFF78909C), Color(0xFF455A64), 1.5f, 1.1f, 1.4f),
    DEMON_HELLHOUND("Hellhound", false, Color(0xFFD84315), Color(0xFFFF5722), 1.3f, 1.35f, 1.0f),
    DRAGON_WHELP("Drake Whelp", false, Color(0xFFC62828), Color(0xFFE53935), 1.4f, 1.4f, 1.1f),

    // BOSSES (Every 5 stages)
    BOSS_TREANT_KING("Elder Treant King", true, Color(0xFF2E7D32), Color(0xFF1B5E20), 4.5f, 1.5f, 2.0f),
    BOSS_GOBLIN_WARLORD("Goblin Warlord", true, Color(0xFF33691E), Color(0xFF558B2F), 5.0f, 1.7f, 2.2f),
    BOSS_GOLEM_COLOSSUS("Ancient Runic Golem", true, Color(0xFF455A64), Color(0xFF00ACC1), 6.0f, 1.8f, 2.8f),
    BOSS_FROST_WYRM("Glacial Frost Wyrm", true, Color(0xFF0288D1), Color(0xFF80D8FF), 6.5f, 2.0f, 2.2f),
    BOSS_DEMON_LORD("Archdemon Malakor", true, Color(0xFFB71C1C), Color(0xFFFF1744), 7.5f, 2.3f, 2.5f),
    BOSS_PHARAOH_ANUBIS("Anubis Death Lord", true, Color(0xFFFFB300), Color(0xFF37474F), 8.0f, 2.4f, 2.6f),
    BOSS_CRIMSON_DRAGON("Cataclysm Dragon", true, Color(0xFFD50000), Color(0xFFFF6D00), 9.0f, 2.6f, 3.0f),
    BOSS_SKY_GRIFFIN("Celestial Griffin King", true, Color(0xFF00B0FF), Color(0xFFFFD600), 9.5f, 2.7f, 3.0f),
    BOSS_SHADOW_MONARCH("Shadow Monarch Void", true, Color(0xFF311B92), Color(0xFF7C4DFF), 10.5f, 2.9f, 3.2f),
    BOSS_CELESTIAL_EMPEROR("Seraphim Creator", true, Color(0xFFFFD700), Color(0xFFFFFFFF), 12.0f, 3.2f, 3.5f)
}

data class EnemyInstance(
    val name: String,
    val type: MonsterType,
    val level: Int,
    val maxHp: Long,
    var currentHp: Long,
    val attack: Long,
    val defense: Long,
    val attackSpeed: Float = 0.9f,
    val goldReward: Long,
    val xpReward: Long,
    val isBoss: Boolean = false,
    val bossSpecialTitle: String = ""
)

data class WorldInfo(
    val id: Int,
    val name: String,
    val description: String,
    val startStage: Int,
    val endStage: Int,
    val bossType: MonsterType,
    val themeColorPrimary: Color,
    val themeColorSecondary: Color
)

object WorldCatalog {
    val WORLDS = listOf(
        WorldInfo(
            id = 1,
            name = "Forgotten Forest",
            description = "A lush ancient woodland guarded by enchanted flora and sly wild beasts.",
            startStage = 1,
            endStage = 5,
            bossType = MonsterType.BOSS_TREANT_KING,
            themeColorPrimary = Color(0xFF1B5E20),
            themeColorSecondary = Color(0xFF2E7D32)
        ),
        WorldInfo(
            id = 2,
            name = "Goblin Kingdom",
            description = "Subterranean trenches and wooden stockades built by the Goblin horde.",
            startStage = 6,
            endStage = 10,
            bossType = MonsterType.BOSS_GOBLIN_WARLORD,
            themeColorPrimary = Color(0xFF33691E),
            themeColorSecondary = Color(0xFF558B2F)
        ),
        WorldInfo(
            id = 3,
            name = "Ancient Ruins",
            description = "Crumbling arcane temples covered in forgotten sigils and stone constructs.",
            startStage = 11,
            endStage = 15,
            bossType = MonsterType.BOSS_GOLEM_COLOSSUS,
            themeColorPrimary = Color(0xFF37474F),
            themeColorSecondary = Color(0xFF00838F)
        ),
        WorldInfo(
            id = 4,
            name = "Frozen Mountains",
            description = "Blizzard swept crags where prehistoric frost beasts sleep beneath glaciers.",
            startStage = 16,
            endStage = 20,
            bossType = MonsterType.BOSS_FROST_WYRM,
            themeColorPrimary = Color(0xFF01579B),
            themeColorSecondary = Color(0xFF0288D1)
        ),
        WorldInfo(
            id = 5,
            name = "Demon Castle",
            description = "Gothic citadel of black iron surrounded by bubbling rivers of magma.",
            startStage = 21,
            endStage = 25,
            bossType = MonsterType.BOSS_DEMON_LORD,
            themeColorPrimary = Color(0xFF880E4F),
            themeColorSecondary = Color(0xFFB71C1C)
        ),
        WorldInfo(
            id = 6,
            name = "Desert Empire",
            description = "Golden dunes hiding cursed pyramid tombs of ancient immortal god-kings.",
            startStage = 26,
            endStage = 30,
            bossType = MonsterType.BOSS_PHARAOH_ANUBIS,
            themeColorPrimary = Color(0xFFE65100),
            themeColorSecondary = Color(0xFFFFB300)
        ),
        WorldInfo(
            id = 7,
            name = "Dragon Valley",
            description = "Smoking sulfur chasms where ancient wyrms hoard mountain-sized treasures.",
            startStage = 31,
            endStage = 35,
            bossType = MonsterType.BOSS_CRIMSON_DRAGON,
            themeColorPrimary = Color(0xFFBF360C),
            themeColorSecondary = Color(0xFFD50000)
        ),
        WorldInfo(
            id = 8,
            name = "Floating Islands",
            description = "Anti-gravity archipelagos suspended high in the clouds above thunder.",
            startStage = 36,
            endStage = 40,
            bossType = MonsterType.BOSS_SKY_GRIFFIN,
            themeColorPrimary = Color(0xFF0D47A1),
            themeColorSecondary = Color(0xFF00B0FF)
        ),
        WorldInfo(
            id = 9,
            name = "Shadow Realm",
            description = "A dimension beyond reality consuming all light into ethereal black holes.",
            startStage = 41,
            endStage = 45,
            bossType = MonsterType.BOSS_SHADOW_MONARCH,
            themeColorPrimary = Color(0xFF1A237E),
            themeColorSecondary = Color(0xFF4A148C)
        ),
        WorldInfo(
            id = 10,
            name = "Celestial Kingdom",
            description = "The throne room of creation built from starlight, diamonds, and holy radiance.",
            startStage = 46,
            endStage = 50,
            bossType = MonsterType.BOSS_CELESTIAL_EMPEROR,
            themeColorPrimary = Color(0xFFF57F17),
            themeColorSecondary = Color(0xFFFFD600)
        )
    )

    fun getWorldForStage(stage: Int): WorldInfo {
        return WORLDS.find { stage in it.startStage..it.endStage } ?: WORLDS.last()
    }

    fun spawnEnemyForStage(stage: Int, wave: Int): EnemyInstance {
        val world = getWorldForStage(stage)
        val isBossWave = (stage % 5 == 0) && (wave >= 5) // Wave 5 on milestone stage is a Boss!
        val enemyType: MonsterType = if (isBossWave) {
            world.bossType
        } else {
            // Pick appropriate regular monsters based on world
            val pool = when (world.id) {
                1 -> listOf(MonsterType.SLIME, MonsterType.WOLF)
                2 -> listOf(MonsterType.GOBLIN, MonsterType.WOLF, MonsterType.SLIME)
                3 -> listOf(MonsterType.SKELETON, MonsterType.UNDEAD_WARRIOR)
                4 -> listOf(MonsterType.WOLF, MonsterType.GARGOYLE, MonsterType.SKELETON)
                5 -> listOf(MonsterType.DEMON_HELLHOUND, MonsterType.DARK_MAGE, MonsterType.ORC)
                6 -> listOf(MonsterType.SKELETON, MonsterType.UNDEAD_WARRIOR, MonsterType.DARK_MAGE)
                7 -> listOf(MonsterType.DRAGON_WHELP, MonsterType.DEMON_HELLHOUND, MonsterType.GARGOYLE)
                8 -> listOf(MonsterType.GARGOYLE, MonsterType.DRAGON_WHELP, MonsterType.DARK_MAGE)
                9 -> listOf(MonsterType.DARK_MAGE, MonsterType.UNDEAD_WARRIOR, MonsterType.DEMON_HELLHOUND)
                else -> listOf(MonsterType.DARK_MAGE, MonsterType.DRAGON_WHELP, MonsterType.ORC)
            }
            pool.random()
        }

        val baseHp = 120.0 * Math.pow(1.18, (stage - 1).toDouble()) * enemyType.hpScale
        val baseAtk = 18.0 * Math.pow(1.15, (stage - 1).toDouble()) * enemyType.atkScale
        val baseDef = 10.0 * Math.pow(1.14, (stage - 1).toDouble()) * enemyType.defScale

        val goldReward = (15.0 * Math.pow(1.14, (stage - 1).toDouble()) * if (isBossWave) 5.0 else 1.0).toLong().coerceAtLeast(10)
        val xpReward = (22.0 * Math.pow(1.15, (stage - 1).toDouble()) * if (isBossWave) 6.0 else 1.0).toLong().coerceAtLeast(15)

        val finalHp = baseHp.toLong().coerceAtLeast(50)
        val finalAtk = baseAtk.toLong().coerceAtLeast(8)
        val finalDef = baseDef.toLong().coerceAtLeast(2)

        return EnemyInstance(
            name = if (isBossWave) "★ ${enemyType.displayName} ★" else enemyType.displayName,
            type = enemyType,
            level = stage,
            maxHp = finalHp,
            currentHp = finalHp,
            attack = finalAtk,
            defense = finalDef,
            attackSpeed = if (isBossWave) 0.75f else 0.9f,
            goldReward = goldReward,
            xpReward = xpReward,
            isBoss = isBossWave,
            bossSpecialTitle = if (isBossWave) "RULER OF ${world.name.uppercase()}" else ""
        )
    }
}
