package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.idlelegends.model.EquipSlot
import com.example.idlelegends.model.EquipmentItem
import com.example.idlelegends.model.HeroClass
import com.example.idlelegends.model.WorldCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Idle Legends", appName)
  }

  @Test
  fun `verify world catalog and stage progression`() {
    assertEquals(10, WorldCatalog.WORLDS.size)
    val world1 = WorldCatalog.getWorldForStage(1)
    assertEquals("Forgotten Forest", world1.name)

    val enemy1 = WorldCatalog.spawnEnemyForStage(stage = 1, wave = 1)
    assertNotNull(enemy1)
    assertTrue(enemy1.maxHp > 0)
    assertTrue(enemy1.attack > 0)

    val bossStage5 = WorldCatalog.spawnEnemyForStage(stage = 5, wave = 5)
    assertTrue(bossStage5.isBoss)
  }

  @Test
  fun `verify hero classes and equipment generation`() {
    assertEquals(5, HeroClass.values().size)
    val warrior = HeroClass.WARRIOR
    assertTrue(warrior.defaultSkills.isNotEmpty())

    val loot = EquipmentItem.generateRandomLoot(stage = 3)
    assertNotNull(loot)
    assertTrue(loot.totalAttack > 0 || loot.totalDefense > 0 || loot.totalHp > 0)
  }

  @Test
  fun `verify combat log action types and categorization`() {
    val attackLog = com.example.idlelegends.model.CombatLog("Hero struck Goblin for 42 damage", com.example.idlelegends.model.CombatActionType.HERO_ATTACK)
    val critLog = com.example.idlelegends.model.CombatLog("CRIT! Struck Boss for 150 damage", com.example.idlelegends.model.CombatActionType.HERO_CRIT)
    val lootLog = com.example.idlelegends.model.CombatLog("LOOT! Found Mythic Blade", com.example.idlelegends.model.CombatActionType.LOOT_DROP)

    assertEquals(com.example.idlelegends.model.CombatActionType.HERO_ATTACK, attackLog.type)
    assertEquals(com.example.idlelegends.model.CombatActionType.HERO_CRIT, critLog.type)
    assertEquals(com.example.idlelegends.model.CombatActionType.LOOT_DROP, lootLog.type)
    assertTrue(attackLog.timestamp > 0)
  }
}

