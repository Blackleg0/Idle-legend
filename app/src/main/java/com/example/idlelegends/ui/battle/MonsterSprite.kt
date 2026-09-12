package com.example.idlelegends.ui.battle

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.idlelegends.model.MonsterType

@Composable
fun MonsterSprite(
    type: MonsterType,
    isAttacking: Boolean,
    isHit: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = if (type.isBoss) 170.dp else 125.dp
) {
    val breathAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        breathAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val lungeAnim = remember { Animatable(0f) }
    LaunchedEffect(isAttacking) {
        if (isAttacking) {
            lungeAnim.snapTo(0f)
            lungeAnim.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0f at 0
                    1f at 130
                    0f at 350
                }
            )
        }
    }

    val hitFlash = remember { Animatable(0f) }
    LaunchedEffect(isHit) {
        if (isHit) {
            hitFlash.snapTo(1f)
            hitFlash.animateTo(0f, tween(180))
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val bounceY = breathAnim.value * (h * 0.04f)
            val lungeX = -lungeAnim.value * (w * 0.22f) // Monsters lunge left toward hero

            // Monster floor shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x77000000), Color.Transparent),
                    center = Offset(w * 0.5f + lungeX, h * 0.92f),
                    radius = w * 0.4f
                ),
                topLeft = Offset(w * 0.15f + lungeX, h * 0.85f),
                size = Size(w * 0.7f, h * 0.16f)
            )

            val hit = hitFlash.value
            val p = lungeAnim.value

            when (type) {
                MonsterType.SLIME -> drawSlime(w, h, bounceY, lungeX, hit)
                MonsterType.WOLF -> drawWolf(w, h, bounceY, lungeX, hit)
                MonsterType.GOBLIN -> drawGoblin(w, h, bounceY, lungeX, hit, p)
                MonsterType.SKELETON -> drawSkeleton(w, h, bounceY, lungeX, hit, p)
                MonsterType.ORC -> drawOrc(w, h, bounceY, lungeX, hit, p)
                MonsterType.UNDEAD_WARRIOR -> drawUndeadWarrior(w, h, bounceY, lungeX, hit, p)
                MonsterType.DARK_MAGE -> drawDarkMage(w, h, bounceY, lungeX, hit, p)
                MonsterType.GARGOYLE -> drawGargoyle(w, h, bounceY, lungeX, hit)
                MonsterType.DEMON_HELLHOUND -> drawHellhound(w, h, bounceY, lungeX, hit)
                MonsterType.DRAGON_WHELP -> drawDragonWhelp(w, h, bounceY, lungeX, hit, p)
                // Bosses
                MonsterType.BOSS_TREANT_KING -> drawTreantBoss(w, h, bounceY, lungeX, hit)
                MonsterType.BOSS_GOBLIN_WARLORD -> drawGoblinWarlord(w, h, bounceY, lungeX, hit, p)
                MonsterType.BOSS_GOLEM_COLOSSUS -> drawGolemBoss(w, h, bounceY, lungeX, hit)
                MonsterType.BOSS_FROST_WYRM -> drawFrostWyrmBoss(w, h, bounceY, lungeX, hit)
                MonsterType.BOSS_DEMON_LORD -> drawDemonLordBoss(w, h, bounceY, lungeX, hit, p)
                MonsterType.BOSS_PHARAOH_ANUBIS -> drawAnubisBoss(w, h, bounceY, lungeX, hit)
                MonsterType.BOSS_CRIMSON_DRAGON -> drawCrimsonDragonBoss(w, h, bounceY, lungeX, hit, p)
                MonsterType.BOSS_SKY_GRIFFIN -> drawGriffinBoss(w, h, bounceY, lungeX, hit)
                MonsterType.BOSS_SHADOW_MONARCH -> drawShadowMonarchBoss(w, h, bounceY, lungeX, hit)
                MonsterType.BOSS_CELESTIAL_EMPEROR -> drawCelestialBoss(w, h, bounceY, lungeX, hit)
            }
        }
    }
}

// SLIME
private fun DrawScope.drawSlime(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.55f + bounce
    val color = if (hit > 0.3f) Color.White else Color(0xFF4CAF50)
    val highlight = if (hit > 0.3f) Color.White else Color(0xFF81C784)

    val path = Path().apply {
        moveTo(cx - w * 0.3f, cy + h * 0.25f)
        quadraticTo(cx - w * 0.38f, cy - h * 0.05f, cx, cy - h * 0.25f)
        quadraticTo(cx + w * 0.38f, cy - h * 0.05f, cx + w * 0.3f, cy + h * 0.25f)
        close()
    }
    drawPath(path, color)

    // Gloss highlight
    drawCircle(highlight, radius = w * 0.08f, center = Offset(cx - w * 0.12f, cy - h * 0.12f))

    // Angry eyes
    drawCircle(Color(0xFFB71C1C), radius = 6f, center = Offset(cx - w * 0.1f, cy + h * 0.02f))
    drawCircle(Color(0xFFB71C1C), radius = 6f, center = Offset(cx + w * 0.1f, cy + h * 0.02f))
}

// WOLF
private fun DrawScope.drawWolf(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val color = if (hit > 0.3f) Color.White else Color(0xFF455A64)

    // Body
    drawRoundRect(color, Offset(cx - w * 0.25f, cy - h * 0.1f), Size(w * 0.5f, h * 0.35f), CornerRadius(16f, 16f))
    // Snout / Head
    val headPath = Path().apply {
        moveTo(cx - w * 0.1f, cy - h * 0.3f)
        lineTo(cx - w * 0.35f, cy - h * 0.05f) // Snout pointing left
        lineTo(cx - w * 0.1f, cy + h * 0.1f)
        close()
    }
    drawPath(headPath, color)
    // Pointy ears
    drawLine(color, Offset(cx - w * 0.1f, cy - h * 0.25f), Offset(cx - w * 0.18f, cy - h * 0.42f), 10f)
    drawLine(color, Offset(cx + w * 0.05f, cy - h * 0.25f), Offset(cx + w * 0.02f, cy - h * 0.42f), 10f)
    // Glowing red eye
    drawCircle(Color(0xFFFF1744), radius = 5f, center = Offset(cx - w * 0.18f, cy - h * 0.12f))
    // Sharp teeth
    drawLine(Color.White, Offset(cx - w * 0.28f, cy - h * 0.02f), Offset(cx - w * 0.25f, cy + h * 0.05f), 4f)
}

// GOBLIN
private fun DrawScope.drawGoblin(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val skin = if (hit > 0.3f) Color.White else Color(0xFF689F38)

    // Goblin body & rags
    drawRoundRect(Color(0xFF5D4037), Offset(cx - w * 0.16f, cy - h * 0.05f), Size(w * 0.32f, h * 0.32f), CornerRadius(12f, 12f))
    // Head
    drawCircle(skin, radius = w * 0.18f, center = Offset(cx, cy - h * 0.18f))
    // Huge pointed goblin ears
    val earL = Path().apply {
        moveTo(cx - w * 0.12f, cy - h * 0.18f)
        lineTo(cx - w * 0.38f, cy - h * 0.28f)
        lineTo(cx - w * 0.12f, cy - h * 0.1f)
        close()
    }
    val earR = Path().apply {
        moveTo(cx + w * 0.12f, cy - h * 0.18f)
        lineTo(cx + w * 0.38f, cy - h * 0.28f)
        lineTo(cx + w * 0.12f, cy - h * 0.1f)
        close()
    }
    drawPath(earL, skin)
    drawPath(earR, skin)
    // Beady yellow eyes
    drawCircle(Color(0xFFFFEB3B), radius = 5f, center = Offset(cx - w * 0.08f, cy - h * 0.18f))
    drawCircle(Color(0xFFFFEB3B), radius = 5f, center = Offset(cx + w * 0.04f, cy - h * 0.18f))
    // Jagged dagger
    drawLine(Color(0xFFB0BEC5), Offset(cx - w * 0.15f, cy + h * 0.1f), Offset(cx - w * 0.35f - p * 20f, cy + h * 0.02f), 8f)
}

// SKELETON
private fun DrawScope.drawSkeleton(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val bone = if (hit > 0.3f) Color.White else Color(0xFFECEFF1)

    // Ribcage & spine
    drawLine(bone, Offset(cx, cy - h * 0.1f), Offset(cx, cy + h * 0.25f), 6f)
    drawLine(bone, Offset(cx - w * 0.12f, cy), Offset(cx + w * 0.12f, cy), 5f)
    drawLine(bone, Offset(cx - w * 0.1f, cy + h * 0.08f), Offset(cx + w * 0.1f, cy + h * 0.08f), 5f)
    // Skull
    drawCircle(bone, radius = w * 0.16f, center = Offset(cx, cy - h * 0.22f))
    // Empty black eye sockets
    drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(cx - w * 0.07f, cy - h * 0.22f))
    drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(cx + w * 0.07f, cy - h * 0.22f))
    // Bone arm holding rusty sword
    drawLine(bone, Offset(cx - w * 0.1f, cy), Offset(cx - w * 0.25f, cy + h * 0.1f), 6f)
    drawLine(Color(0xFF8D6E63), Offset(cx - w * 0.25f, cy + h * 0.1f), Offset(cx - w * 0.42f - p * 25f, cy - h * 0.15f), 7f)
}

// ORC
private fun DrawScope.drawOrc(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val skin = if (hit > 0.3f) Color.White else Color(0xFF558B2F)

    // Muscular wide body
    drawRoundRect(skin, Offset(cx - w * 0.24f, cy - h * 0.12f), Size(w * 0.48f, h * 0.4f), CornerRadius(18f, 18f))
    // Head with jaw
    drawRoundRect(skin, Offset(cx - w * 0.16f, cy - h * 0.32f), Size(w * 0.32f, h * 0.24f), CornerRadius(14f, 14f))
    // Tusks curving upwards
    drawLine(Color(0xFFFFF9C4), Offset(cx - w * 0.1f, cy - h * 0.12f), Offset(cx - w * 0.14f, cy - h * 0.2f), 6f)
    drawLine(Color(0xFFFFF9C4), Offset(cx + w * 0.1f, cy - h * 0.12f), Offset(cx + w * 0.14f, cy - h * 0.2f), 6f)
    // Red glowing eyes
    drawCircle(Color(0xFFFF1744), radius = 5f, center = Offset(cx - w * 0.06f, cy - h * 0.24f))
    drawCircle(Color(0xFFFF1744), radius = 5f, center = Offset(cx + w * 0.06f, cy - h * 0.24f))
    // Spiked wooden club
    drawLine(Color(0xFF4E342E), Offset(cx - w * 0.2f, cy + h * 0.1f), Offset(cx - w * 0.4f - p * 25f, cy - h * 0.15f), 12f)
}

// UNDEAD WARRIOR
private fun DrawScope.drawUndeadWarrior(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val armor = if (hit > 0.3f) Color.White else Color(0xFF37474F)

    drawRoundRect(armor, Offset(cx - w * 0.2f, cy - h * 0.1f), Size(w * 0.4f, h * 0.38f), CornerRadius(14f, 14f))
    drawRoundRect(Color(0xFF263238), Offset(cx - w * 0.15f, cy - h * 0.32f), Size(w * 0.3f, h * 0.24f), CornerRadius(14f, 14f))
    // Cyan ghostly eye glow
    drawCircle(Color(0xFF18FFFF), radius = 5f, center = Offset(cx - w * 0.06f, cy - h * 0.2f))
    drawCircle(Color(0xFF18FFFF), radius = 5f, center = Offset(cx + w * 0.06f, cy - h * 0.2f))
    // Spectral blade
    drawLine(Color(0xFF00E5FF), Offset(cx - w * 0.18f, cy + h * 0.05f), Offset(cx - w * 0.38f - p * 20f, cy - h * 0.15f), 8f)
}

// DARK MAGE
private fun DrawScope.drawDarkMage(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val robe = if (hit > 0.3f) Color.White else Color(0xFF4A148C)

    val path = Path().apply {
        moveTo(cx - w * 0.18f, cy - h * 0.2f)
        lineTo(cx + w * 0.18f, cy - h * 0.2f)
        lineTo(cx + w * 0.26f, cy + h * 0.36f)
        lineTo(cx - w * 0.26f, cy + h * 0.36f)
        close()
    }
    drawPath(path, robe)
    // Dark void inside hood
    drawCircle(Color(0xFF1A0033), radius = w * 0.14f, center = Offset(cx, cy - h * 0.2f))
    drawCircle(Color(0xFFFF00FF), radius = 4f, center = Offset(cx - w * 0.05f, cy - h * 0.2f))
    drawCircle(Color(0xFFFF00FF), radius = 4f, center = Offset(cx + w * 0.05f, cy - h * 0.2f))
    // Floating purple void orb
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFE040FB), Color(0xFF7C4DFF), Color.Transparent), center = Offset(cx - w * 0.28f, cy - h * 0.1f), radius = w * 0.18f),
        radius = w * 0.16f,
        center = Offset(cx - w * 0.28f, cy - h * 0.1f)
    )
}

// GARGOYLE
private fun DrawScope.drawGargoyle(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val stone = if (hit > 0.3f) Color.White else Color(0xFF78909C)

    // Stone wings
    val wingL = Path().apply {
        moveTo(cx - w * 0.1f, cy - h * 0.1f)
        lineTo(cx - w * 0.42f, cy - h * 0.38f)
        lineTo(cx - w * 0.28f, cy + h * 0.05f)
        close()
    }
    drawPath(wingL, Color(0xFF546E7A))
    // Body
    drawRoundRect(stone, Offset(cx - w * 0.2f, cy - h * 0.1f), Size(w * 0.4f, h * 0.38f), CornerRadius(16f, 16f))
    // Horned head
    drawRoundRect(stone, Offset(cx - w * 0.15f, cy - h * 0.3f), Size(w * 0.3f, h * 0.22f), CornerRadius(12f, 12f))
    // Glowing yellow eyes
    drawCircle(Color(0xFFFFEA00), radius = 5f, center = Offset(cx - w * 0.06f, cy - h * 0.18f))
    drawCircle(Color(0xFFFFEA00), radius = 5f, center = Offset(cx + w * 0.06f, cy - h * 0.18f))
}

// HELLHOUND
private fun DrawScope.drawHellhound(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val body = if (hit > 0.3f) Color.White else Color(0xFFBF360C)

    drawRoundRect(body, Offset(cx - w * 0.28f, cy - h * 0.08f), Size(w * 0.56f, h * 0.36f), CornerRadius(16f, 16f))
    // Snout
    val snout = Path().apply {
        moveTo(cx - w * 0.12f, cy - h * 0.25f)
        lineTo(cx - w * 0.38f, cy - h * 0.05f)
        lineTo(cx - w * 0.12f, cy + h * 0.1f)
        close()
    }
    drawPath(snout, body)
    // Burning fire mane
    drawCircle(Color(0xFFFF5722), radius = w * 0.14f, center = Offset(cx + w * 0.05f, cy - h * 0.15f))
    drawCircle(Color(0xFFFFEB3B), radius = w * 0.08f, center = Offset(cx + w * 0.05f, cy - h * 0.15f))
    // Fiery eye
    drawCircle(Color(0xFFFFEA00), radius = 5f, center = Offset(cx - w * 0.2f, cy - h * 0.12f))
}

// DRAGON WHELP
private fun DrawScope.drawDragonWhelp(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val scale = if (hit > 0.3f) Color.White else Color(0xFFC62828)

    // Red wings
    val wing = Path().apply {
        moveTo(cx + w * 0.05f, cy - h * 0.1f)
        lineTo(cx + w * 0.42f, cy - h * 0.4f)
        lineTo(cx + w * 0.25f, cy + h * 0.1f)
        close()
    }
    drawPath(wing, Color(0xFFB71C1C))
    // Body
    drawRoundRect(scale, Offset(cx - w * 0.2f, cy - h * 0.1f), Size(w * 0.4f, h * 0.38f), CornerRadius(16f, 16f))
    // Head with horns
    drawCircle(scale, radius = w * 0.18f, center = Offset(cx - w * 0.1f, cy - h * 0.2f))
    drawLine(Color(0xFFFFD54F), Offset(cx - w * 0.1f, cy - h * 0.28f), Offset(cx - w * 0.05f, cy - h * 0.42f), 6f)
    // Slit eye
    drawCircle(Color(0xFFFFEA00), radius = 5f, center = Offset(cx - w * 0.16f, cy - h * 0.2f))
    // Fire breath spark when attacking
    if (p > 0.15f) {
        drawCircle(Color(0xFFFF5722), radius = 12f, center = Offset(cx - w * 0.35f, cy - h * 0.15f))
        drawCircle(Color(0xFFFFD600), radius = 7f, center = Offset(cx - w * 0.35f, cy - h * 0.15f))
    }
}

// BOSSES (TREANT, GOBLIN WARLORD, GOLEM, FROST WYRM, DEMON LORD, ETC.)
private fun DrawScope.drawTreantBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val bark = if (hit > 0.3f) Color.White else Color(0xFF3E2723)

    // Massive trunk
    drawRoundRect(bark, Offset(cx - w * 0.3f, cy - h * 0.15f), Size(w * 0.6f, h * 0.5f), CornerRadius(20f, 20f))
    // Glowing foliage crown
    drawCircle(Color(0xFF2E7D32), radius = w * 0.3f, center = Offset(cx, cy - h * 0.25f))
    drawCircle(Color(0xFF4CAF50), radius = w * 0.2f, center = Offset(cx - w * 0.08f, cy - h * 0.3f))
    // Glowing emerald eyes
    drawCircle(Color(0xFF00E676), radius = 7f, center = Offset(cx - w * 0.12f, cy - h * 0.05f))
    drawCircle(Color(0xFF00E676), radius = 7f, center = Offset(cx + w * 0.12f, cy - h * 0.05f))
    // Ancient runic moss
    drawLine(Color(0xFF76FF03), Offset(cx - w * 0.15f, cy + h * 0.12f), Offset(cx + w * 0.1f, cy + h * 0.18f), 6f)
}

private fun DrawScope.drawGoblinWarlord(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val skin = if (hit > 0.3f) Color.White else Color(0xFF33691E)

    // Armored body
    drawRoundRect(Color(0xFF3E2723), Offset(cx - w * 0.28f, cy - h * 0.12f), Size(w * 0.56f, h * 0.45f), CornerRadius(18f, 18f))
    // Big chieftain head
    drawCircle(skin, radius = w * 0.22f, center = Offset(cx, cy - h * 0.22f))
    // Golden crown with spikes
    val crown = Path().apply {
        moveTo(cx - w * 0.2f, cy - h * 0.35f)
        lineTo(cx - w * 0.15f, cy - h * 0.46f)
        lineTo(cx, cy - h * 0.38f)
        lineTo(cx + w * 0.15f, cy - h * 0.46f)
        lineTo(cx + w * 0.2f, cy - h * 0.35f)
        close()
    }
    drawPath(crown, Color(0xFFFFD700))
    // Red glowing eyes
    drawCircle(Color(0xFFFF1744), radius = 6f, center = Offset(cx - w * 0.09f, cy - h * 0.2f))
    drawCircle(Color(0xFFFF1744), radius = 6f, center = Offset(cx + w * 0.09f, cy - h * 0.2f))
    // Spiked War Battleaxe
    drawLine(Color(0xFFCFD8DC), Offset(cx - w * 0.25f, cy + h * 0.1f), Offset(cx - w * 0.45f - p * 30f, cy - h * 0.2f), 14f)
}

private fun DrawScope.drawGolemBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val stone = if (hit > 0.3f) Color.White else Color(0xFF37474F)

    // Giant stone monolith body
    drawRoundRect(stone, Offset(cx - w * 0.32f, cy - h * 0.18f), Size(w * 0.64f, h * 0.52f), CornerRadius(22f, 22f))
    // Glowing cyan runic lines across chest
    drawLine(Color(0xFF00E5FF), Offset(cx - w * 0.2f, cy), Offset(cx + w * 0.2f, cy), 7f)
    drawLine(Color(0xFF00E5FF), Offset(cx, cy - h * 0.1f), Offset(cx, cy + h * 0.2f), 7f)
    // Single giant eye core
    drawCircle(Color(0xFF00E5FF), radius = w * 0.1f, center = Offset(cx, cy - h * 0.25f))
    drawCircle(Color.White, radius = w * 0.04f, center = Offset(cx, cy - h * 0.25f))
}

private fun DrawScope.drawFrostWyrmBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val ice = if (hit > 0.3f) Color.White else Color(0xFF0288D1)

    // Glacial wings
    val wingL = Path().apply {
        moveTo(cx - w * 0.1f, cy - h * 0.1f)
        lineTo(cx - w * 0.48f, cy - h * 0.42f)
        lineTo(cx - w * 0.32f, cy + h * 0.15f)
        close()
    }
    drawPath(wingL, Color(0xFF80D8FF))
    // Body
    drawRoundRect(ice, Offset(cx - w * 0.25f, cy - h * 0.1f), Size(w * 0.5f, h * 0.45f), CornerRadius(20f, 20f))
    // Dragon head with crystal horns
    drawCircle(ice, radius = w * 0.2f, center = Offset(cx - w * 0.1f, cy - h * 0.25f))
    drawLine(Color(0xFFE1F5FE), Offset(cx - w * 0.1f, cy - h * 0.35f), Offset(cx - w * 0.05f, cy - h * 0.5f), 8f)
    // Icy glow eye
    drawCircle(Color(0xFFB3E5FC), radius = 6f, center = Offset(cx - w * 0.18f, cy - h * 0.25f))
}

private fun DrawScope.drawDemonLordBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val flesh = if (hit > 0.3f) Color.White else Color(0xFFB71C1C)

    // Flaming bat wings
    val wing = Path().apply {
        moveTo(cx, cy - h * 0.1f)
        lineTo(cx + w * 0.45f, cy - h * 0.45f)
        lineTo(cx + w * 0.35f, cy + h * 0.1f)
        close()
    }
    drawPath(wing, Color(0xFF212121))
    // Crimson body
    drawRoundRect(flesh, Offset(cx - w * 0.28f, cy - h * 0.15f), Size(w * 0.56f, h * 0.5f), CornerRadius(22f, 22f))
    // Curved demon horns
    val hornL = Path().apply {
        moveTo(cx - w * 0.12f, cy - h * 0.32f)
        quadraticTo(cx - w * 0.32f, cy - h * 0.45f, cx - w * 0.28f, cy - h * 0.52f)
    }
    val hornR = Path().apply {
        moveTo(cx + w * 0.12f, cy - h * 0.32f)
        quadraticTo(cx + w * 0.32f, cy - h * 0.45f, cx + w * 0.28f, cy - h * 0.52f)
    }
    drawPath(hornL, Color(0xFF121212), style = Stroke(width = 12f))
    drawPath(hornR, Color(0xFF121212), style = Stroke(width = 12f))
    // Flaming eyes
    drawCircle(Color(0xFFFFEA00), radius = 7f, center = Offset(cx - w * 0.1f, cy - h * 0.25f))
    drawCircle(Color(0xFFFFEA00), radius = 7f, center = Offset(cx + w * 0.1f, cy - h * 0.25f))
}

private fun DrawScope.drawAnubisBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val body = if (hit > 0.3f) Color.White else Color(0xFF212121)

    drawRoundRect(body, Offset(cx - w * 0.26f, cy - h * 0.12f), Size(w * 0.52f, h * 0.46f), CornerRadius(18f, 18f))
    // Gold Nemes Headdress
    drawRect(Color(0xFFFFB300), Offset(cx - w * 0.22f, cy - h * 0.38f), Size(w * 0.44f, h * 0.28f))
    // Jackal ears
    drawLine(Color(0xFF212121), Offset(cx - w * 0.15f, cy - h * 0.38f), Offset(cx - w * 0.22f, cy - h * 0.52f), 12f)
    drawLine(Color(0xFF212121), Offset(cx + w * 0.15f, cy - h * 0.38f), Offset(cx + w * 0.22f, cy - h * 0.52f), 12f)
    // Golden eyes
    drawCircle(Color(0xFFFFD700), radius = 6f, center = Offset(cx - w * 0.08f, cy - h * 0.25f))
    drawCircle(Color(0xFFFFD700), radius = 6f, center = Offset(cx + w * 0.08f, cy - h * 0.25f))
}

private fun DrawScope.drawCrimsonDragonBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float, p: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val scale = if (hit > 0.3f) Color.White else Color(0xFFD50000)

    // Enormous dragon wings
    val wingL = Path().apply {
        moveTo(cx - w * 0.1f, cy - h * 0.1f)
        lineTo(cx - w * 0.48f, cy - h * 0.45f)
        lineTo(cx - w * 0.35f, cy + h * 0.15f)
        close()
    }
    drawPath(wingL, Color(0xFFB71C1C))
    // Massive scaly body
    drawRoundRect(scale, Offset(cx - w * 0.3f, cy - h * 0.15f), Size(w * 0.6f, h * 0.5f), CornerRadius(22f, 22f))
    // Dragon horned head
    drawCircle(scale, radius = w * 0.24f, center = Offset(cx - w * 0.15f, cy - h * 0.25f))
    drawLine(Color(0xFFFFD600), Offset(cx - w * 0.15f, cy - h * 0.35f), Offset(cx - w * 0.08f, cy - h * 0.52f), 10f)
    // Cataclysm golden eye
    drawCircle(Color(0xFFFFD600), radius = 7f, center = Offset(cx - w * 0.22f, cy - h * 0.25f))
}

private fun DrawScope.drawGriffinBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val color = if (hit > 0.3f) Color.White else Color(0xFF00B0FF)

    drawRoundRect(color, Offset(cx - w * 0.26f, cy - h * 0.12f), Size(w * 0.52f, h * 0.46f), CornerRadius(18f, 18f))
    // Eagle beak
    val beak = Path().apply {
        moveTo(cx - w * 0.15f, cy - h * 0.3f)
        lineTo(cx - w * 0.38f, cy - h * 0.18f)
        lineTo(cx - w * 0.15f, cy - h * 0.1f)
        close()
    }
    drawPath(beak, Color(0xFFFFD600))
}

private fun DrawScope.drawShadowMonarchBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce

    // Swirling black hole void
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFF311B92), Color(0xFF1A237E), Color.Transparent), center = Offset(cx, cy), radius = w * 0.48f),
        radius = w * 0.45f,
        center = Offset(cx, cy)
    )
    drawRoundRect(Color(0xFF0D0D1A), Offset(cx - w * 0.24f, cy - h * 0.2f), Size(w * 0.48f, h * 0.5f), CornerRadius(18f, 18f))
    // Glowing violet monarch eyes
    drawCircle(Color(0xFFE040FB), radius = 8f, center = Offset(cx - w * 0.1f, cy - h * 0.18f))
    drawCircle(Color(0xFFE040FB), radius = 8f, center = Offset(cx + w * 0.1f, cy - h * 0.18f))
}

private fun DrawScope.drawCelestialBoss(w: Float, h: Float, bounce: Float, lungeX: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce

    // Radiant holy halo
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFFFF9C4), Color(0xFFFFD700), Color.Transparent), center = Offset(cx, cy), radius = w * 0.5f),
        radius = w * 0.48f,
        center = Offset(cx, cy)
    )
    // Pure divine gold silhouette
    drawRoundRect(Color(0xFFFFD700), Offset(cx - w * 0.25f, cy - h * 0.2f), Size(w * 0.5f, h * 0.5f), CornerRadius(22f, 22f))
    drawCircle(Color.White, radius = 8f, center = Offset(cx - w * 0.08f, cy - h * 0.15f))
    drawCircle(Color.White, radius = 8f, center = Offset(cx + w * 0.08f, cy - h * 0.15f))
}
