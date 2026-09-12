package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.idlelegends.model.HeroClass
import com.example.idlelegends.ui.battle.HeroSprite
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun idle_legends_screenshot() {
    composeTestRule.setContent {
      Box(modifier = Modifier.size(200.dp)) {
        HeroSprite(heroClass = HeroClass.WARRIOR, isAttacking = false, isHit = false)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/hero_preview.png")
  }
}

