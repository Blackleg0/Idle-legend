package com.example.idlelegends.ui.battle

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.idlelegends.model.WorldInfo
import kotlin.math.sin

@Composable
fun WorldBackground(
    world: WorldInfo,
    modifier: Modifier = Modifier
) {
    // Ambient floating particles / mist animation
    val ambientAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        ambientAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = ambientAnim.value

        when (world.id) {
            1 -> drawForestWorld(w, h, t)
            2 -> drawGoblinWorld(w, h, t)
            3 -> drawRuinsWorld(w, h, t)
            4 -> drawFrozenWorld(w, h, t)
            5 -> drawDemonCastleWorld(w, h, t)
            6 -> drawDesertWorld(w, h, t)
            7 -> drawDragonValleyWorld(w, h, t)
            8 -> drawFloatingIslandsWorld(w, h, t)
            9 -> drawShadowRealmWorld(w, h, t)
            10 -> drawCelestialWorld(w, h, t)
            else -> drawForestWorld(w, h, t)
        }
    }
}

// 1. Forgotten Forest
private fun DrawScope.drawForestWorld(w: Float, h: Float, t: Float) {
    // Sky gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0A1F0D), Color(0xFF1B381E), Color(0xFF2E5928)),
            startY = 0f,
            endY = h * 0.7f
        )
    )
    // Distant tree silhouettes
    for (i in 0..5) {
        val tx = i * (w / 4.5f)
        val treePath = Path().apply {
            moveTo(tx, h * 0.65f)
            lineTo(tx + w * 0.12f, h * 0.3f)
            lineTo(tx + w * 0.24f, h * 0.65f)
            close()
        }
        drawPath(treePath, Color(0xFF122814))
    }
    // Mossy Ground
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2E4B27), Color(0xFF1B3117)),
            startY = h * 0.65f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.65f),
        size = Size(w, h * 0.35f)
    )
    // Floating mystical fireflies
    for (i in 0..12) {
        val fx = (w * (0.1f + i * 0.08f) + sin((t + i) * 6.28f) * 20f) % w
        val fy = (h * (0.25f + (i % 5) * 0.08f) - (t * 80f + i * 15f)) % (h * 0.55f) + h * 0.15f
        drawCircle(Color(0xBB76FF03), radius = 3.5f, center = Offset(fx, fy))
        drawCircle(Color(0x4476FF03), radius = 8f, center = Offset(fx, fy))
    }
}

// 2. Goblin Kingdom
private fun DrawScope.drawGoblinWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1A1A0F), Color(0xFF2E2E1A), Color(0xFF3E3924)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Wooden palisade spikes
    for (i in 0..10) {
        val px = i * (w / 8f)
        val spike = Path().apply {
            moveTo(px, h * 0.68f)
            lineTo(px + w * 0.04f, h * 0.45f)
            lineTo(px + w * 0.08f, h * 0.68f)
            close()
        }
        drawPath(spike, Color(0xFF3E2723))
    }
    // Muddy Trench Ground
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF3E2723), Color(0xFF271C19)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f),
        size = Size(w, h * 0.32f)
    )
    // Campfire glow
    val torchGlow = 0.8f + sin(t * 12.5f) * 0.2f
    drawCircle(Color(0x77FF6D00), radius = 30f * torchGlow, center = Offset(w * 0.15f, h * 0.6f))
    drawCircle(Color(0x99FFD600), radius = 15f * torchGlow, center = Offset(w * 0.15f, h * 0.6f))
}

// 3. Ancient Ruins
private fun DrawScope.drawRuinsWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF102027), Color(0xFF263238), Color(0xFF37474F)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Cracked marble pillars
    for (i in listOf(0.12f, 0.45f, 0.82f)) {
        val colX = w * i
        drawRect(Color(0xFF455A64), Offset(colX, h * 0.3f), Size(w * 0.1f, h * 0.38f))
        drawRect(Color(0xFF546E7A), Offset(colX - w * 0.02f, h * 0.28f), Size(w * 0.14f, h * 0.03f))
    }
    // Stone flagged floor
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF37474F), Color(0xFF212121)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f),
        size = Size(w, h * 0.32f)
    )
    // Glowing cyan runic spark particles
    for (i in 0..10) {
        val rx = (w * (0.15f + i * 0.08f) + sin((t + i) * 4f) * 15f) % w
        val ry = (h * (0.35f + (i % 4) * 0.08f) + (t * 30f)) % (h * 0.6f) + h * 0.15f
        drawCircle(Color(0xCC00E5FF), radius = 3f, center = Offset(rx, ry))
    }
}

// 4. Frozen Mountains
private fun DrawScope.drawFrozenWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF002171), Color(0xFF0D47A1), Color(0xFF1976D2)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Mountain peaks
    val mtn = Path().apply {
        moveTo(0f, h * 0.68f)
        lineTo(w * 0.25f, h * 0.25f)
        lineTo(w * 0.5f, h * 0.55f)
        lineTo(w * 0.8f, h * 0.28f)
        lineTo(w, h * 0.68f)
        close()
    }
    drawPath(mtn, Color(0xFF01579B))
    // Snow caps
    val snowCap = Path().apply {
        moveTo(w * 0.18f, h * 0.36f)
        lineTo(w * 0.25f, h * 0.25f)
        lineTo(w * 0.32f, h * 0.36f)
        close()
    }
    drawPath(snowCap, Color(0xFFE1F5FE))
    // Ice floor
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f), size = Size(w, h * 0.32f)
    )
    // Blizzard snowflakes
    for (i in 0..20) {
        val sx = (w * (i * 0.06f) + t * w) % w
        val sy = (h * (0.1f + i * 0.04f) + t * h * 0.8f) % h
        drawCircle(Color(0xCCFFFFFF), radius = 2.5f, center = Offset(sx, sy))
    }
}

// 5. Demon Castle
private fun DrawScope.drawDemonCastleWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1A0000), Color(0xFF3E0A0A), Color(0xFF5D1010)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Gothic spires
    for (i in listOf(0.1f, 0.4f, 0.75f)) {
        val spire = Path().apply {
            moveTo(w * i, h * 0.68f)
            lineTo(w * (i + 0.06f), h * 0.22f)
            lineTo(w * (i + 0.12f), h * 0.68f)
            close()
        }
        drawPath(spire, Color(0xFF212121))
    }
    // Molten lava cracks on obsidian ground
    drawRect(Color(0xFF181818), Offset(0f, h * 0.68f), Size(w, h * 0.32f))
    drawLine(Color(0xFFFF3D00), Offset(0f, h * 0.85f), Offset(w, h * 0.82f), strokeWidth = 8f)
    drawLine(Color(0xFFFFEA00), Offset(w * 0.2f, h * 0.84f), Offset(w * 0.7f, h * 0.83f), strokeWidth = 4f)
    // Floating red fire embers
    for (i in 0..15) {
        val ex = (w * (i * 0.07f) + sin((t + i) * 5f) * 20f) % w
        val ey = (h * 0.9f - (t * h * 0.7f + i * 30f)) % (h * 0.8f) + h * 0.1f
        drawCircle(Color(0xFFFF5722), radius = 3.5f, center = Offset(ex, ey))
    }
}

// 6. Desert Empire
private fun DrawScope.drawDesertWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2A150A), Color(0xFF4E2C14), Color(0xFF7A481F)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Pyramid silhouette
    val pyr = Path().apply {
        moveTo(w * 0.2f, h * 0.68f)
        lineTo(w * 0.5f, h * 0.38f)
        lineTo(w * 0.8f, h * 0.68f)
        close()
    }
    drawPath(pyr, Color(0xFF5D381A))
    // Sand Dunes
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFD4A373), Color(0xFFBC6C25)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f), size = Size(w, h * 0.32f)
    )
}

// 7. Dragon Valley
private fun DrawScope.drawDragonValleyWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF200000), Color(0xFF4A0E00), Color(0xFF7B1E00)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Volcanic mountains
    val volk = Path().apply {
        moveTo(0f, h * 0.68f)
        lineTo(w * 0.4f, h * 0.3f)
        lineTo(w * 0.6f, h * 0.35f)
        lineTo(w, h * 0.68f)
        close()
    }
    drawPath(volk, Color(0xFF3E1F1A))
    // Magma floor
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFB71C1C), Color(0xFF5A0000)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f), size = Size(w, h * 0.32f)
    )
}

// 8. Floating Islands
private fun DrawScope.drawFloatingIslandsWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF001030), Color(0xFF003060), Color(0xFF1060A0)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Distant floating island
    drawOval(Color(0xFF1B5E20), Offset(w * 0.15f, h * 0.38f), Size(w * 0.35f, h * 0.08f))
    drawOval(Color(0xFF2E7D32), Offset(w * 0.55f, h * 0.48f), Size(w * 0.3f, h * 0.06f))
    // Main sky island platform
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF2E7D32), Color(0xFF4E342E)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f), size = Size(w, h * 0.32f)
    )
    // Drifting clouds
    for (i in 0..4) {
        val cx = (w * (i * 0.3f) + t * w * 0.5f) % (w * 1.5f) - w * 0.25f
        val cy = h * (0.15f + i * 0.08f)
        drawCircle(Color(0x33FFFFFF), radius = 35f, center = Offset(cx, cy))
        drawCircle(Color(0x33FFFFFF), radius = 45f, center = Offset(cx + 30f, cy + 5f))
    }
}

// 9. Shadow Realm
private fun DrawScope.drawShadowRealmWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF4A148C), Color(0xFF1A0033), Color(0xFF080010)),
            center = Offset(w * 0.5f, h * 0.4f),
            radius = w * 0.8f
        )
    )
    // Dark void floor
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF311B92), Color(0xFF0D001A)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f), size = Size(w, h * 0.32f)
    )
    // Swirling void particles
    for (i in 0..14) {
        val vx = (w * (i * 0.08f) + sin((t + i) * 6f) * 35f) % w
        val vy = (h * (0.2f + (i % 6) * 0.08f) - t * 60f) % (h * 0.65f) + h * 0.1f
        drawCircle(Color(0xCCBA68C8), radius = 3f, center = Offset(vx, vy))
    }
}

// 10. Celestial Kingdom
private fun DrawScope.drawCelestialWorld(w: Float, h: Float, t: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3E2723), Color(0xFFF57F17), Color(0xFFFFD54F)),
            startY = 0f, endY = h * 0.68f
        )
    )
    // Divine Light Shafts
    for (i in 0..4) {
        val lx = w * (0.1f + i * 0.22f)
        val ray = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(lx, h * 0.68f)
            lineTo(lx + w * 0.1f, h * 0.68f)
            close()
        }
        drawPath(ray, Color(0x22FFFFFF))
    }
    // Golden Palace Floor
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA000)), h * 0.68f, h),
        topLeft = Offset(0f, h * 0.68f), size = Size(w, h * 0.32f)
    )
    // Golden sparkle motes
    for (i in 0..16) {
        val sx = (w * (i * 0.07f) + sin((t + i) * 5f) * 15f) % w
        val sy = (h * (0.2f + (i % 5) * 0.09f) - t * 40f) % (h * 0.65f) + h * 0.1f
        drawCircle(Color(0xEEFFF9C4), radius = 3.5f, center = Offset(sx, sy))
    }
}
