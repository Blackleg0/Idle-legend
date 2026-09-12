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
import com.example.idlelegends.model.HeroClass
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HeroSprite(
    heroClass: HeroClass,
    isAttacking: Boolean,
    isHit: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp
) {
    // Idle breathing & bounce animation
    val bounceAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        bounceAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Attack lunge animation
    val lungeAnim = remember { Animatable(0f) }
    LaunchedEffect(isAttacking) {
        if (isAttacking) {
            lungeAnim.snapTo(0f)
            lungeAnim.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 320
                    0f at 0
                    1f at 120
                    0f at 320
                }
            )
        }
    }

    // Hit flash
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
            val canvasW = this.size.width
            val canvasH = this.size.height
            val bounceY = bounceAnim.value * (canvasH * 0.035f)
            val lungeX = lungeAnim.value * (canvasW * 0.22f)

            // Shadow under hero
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x77000000), Color.Transparent),
                    center = Offset(canvasW * 0.5f + lungeX, canvasH * 0.9f),
                    radius = canvasW * 0.35f
                ),
                topLeft = Offset(canvasW * 0.2f + lungeX, canvasH * 0.82f),
                size = Size(canvasW * 0.6f, canvasH * 0.16f)
            )

            // Draw specific hero class
            when (heroClass) {
                HeroClass.WARRIOR -> drawWarrior(canvasW, canvasH, bounceY, lungeX, lungeAnim.value, hitFlash.value)
                HeroClass.MAGE -> drawMage(canvasW, canvasH, bounceY, lungeX, lungeAnim.value, hitFlash.value)
                HeroClass.ARCHER -> drawArcher(canvasW, canvasH, bounceY, lungeX, lungeAnim.value, hitFlash.value)
                HeroClass.ASSASSIN -> drawAssassin(canvasW, canvasH, bounceY, lungeX, lungeAnim.value, hitFlash.value)
                HeroClass.PALADIN -> drawPaladin(canvasW, canvasH, bounceY, lungeX, lungeAnim.value, hitFlash.value)
            }
        }
    }
}

private fun DrawScope.drawWarrior(w: Float, h: Float, bounce: Float, lungeX: Float, attackProgress: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val hitTint = if (hit > 0.3f) Color.White else Color.Transparent

    // Red Cape
    val capePath = Path().apply {
        moveTo(cx - w * 0.12f, cy - h * 0.1f)
        quadraticTo(cx - w * 0.32f - attackProgress * 20f, cy + h * 0.2f, cx - w * 0.2f, cy + h * 0.35f)
        lineTo(cx - w * 0.05f, cy + h * 0.3f)
        close()
    }
    drawPath(capePath, Color(0xFFC62828))

    // Armor Body
    drawRoundRect(
        color = if (hit > 0.3f) hitTint else Color(0xFF455A64),
        topLeft = Offset(cx - w * 0.16f, cy - h * 0.08f),
        size = Size(w * 0.32f, h * 0.32f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    // Golden chest emblem
    drawCircle(
        color = Color(0xFFFFD700),
        radius = w * 0.05f,
        center = Offset(cx, cy + h * 0.05f)
    )

    // Helmet & Head
    drawRoundRect(
        color = if (hit > 0.3f) hitTint else Color(0xFF37474F),
        topLeft = Offset(cx - w * 0.15f, cy - h * 0.32f),
        size = Size(w * 0.3f, h * 0.25f),
        cornerRadius = CornerRadius(18f, 18f)
    )
    // Helmet Visor T-slit
    drawRect(
        color = Color(0xFF00E5FF),
        topLeft = Offset(cx - w * 0.08f, cy - h * 0.22f),
        size = Size(w * 0.16f, h * 0.04f)
    )
    // Horns on helmet
    val hornPathL = Path().apply {
        moveTo(cx - w * 0.14f, cy - h * 0.28f)
        lineTo(cx - w * 0.25f, cy - h * 0.38f)
        lineTo(cx - w * 0.10f, cy - h * 0.22f)
        close()
    }
    val hornPathR = Path().apply {
        moveTo(cx + w * 0.14f, cy - h * 0.28f)
        lineTo(cx + w * 0.25f, cy - h * 0.38f)
        lineTo(cx + w * 0.10f, cy - h * 0.22f)
        close()
    }
    drawPath(hornPathL, Color(0xFFFFD700))
    drawPath(hornPathR, Color(0xFFFFD700))

    // Heater Shield (Left hand)
    val shieldPath = Path().apply {
        moveTo(cx - w * 0.24f, cy - h * 0.06f)
        lineTo(cx - w * 0.1f, cy - h * 0.06f)
        lineTo(cx - w * 0.12f, cy + h * 0.2f)
        lineTo(cx - w * 0.17f, cy + h * 0.25f)
        lineTo(cx - w * 0.24f, cy + h * 0.16f)
        close()
    }
    drawPath(shieldPath, Color(0xFF1E88E5))
    drawPath(shieldPath, Color(0xFFFFD700), style = Stroke(width = 4f))

    // Claymore Sword (Right hand)
    val swordAngle = -30f + attackProgress * 80f
    val swordBaseX = cx + w * 0.16f
    val swordBaseY = cy + h * 0.04f
    val swordTipX = swordBaseX + cos((swordAngle * PI / 180f).toFloat()) * (w * 0.45f)
    val swordTipY = swordBaseY - sin((swordAngle * PI / 180f).toFloat()) * (h * 0.45f)

    drawLine(
        color = Color(0xFFECEFF1),
        start = Offset(swordBaseX, swordBaseY),
        end = Offset(swordTipX, swordTipY),
        strokeWidth = 10f
    )
    // Crossguard
    drawCircle(Color(0xFFFFD700), radius = 8f, center = Offset(swordBaseX, swordBaseY))

    // Attack slash arc when attacking
    if (attackProgress > 0.1f) {
        drawArc(
            color = Color(0xAAFFFFFF),
            startAngle = -70f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(cx + w * 0.05f, cy - h * 0.45f),
            size = Size(w * 0.6f, h * 0.8f),
            style = Stroke(width = 8f)
        )
    }
}

private fun DrawScope.drawMage(w: Float, h: Float, bounce: Float, lungeX: Float, attackProgress: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val hitTint = if (hit > 0.3f) Color.White else Color.Transparent

    // Arcane Robes
    val robePath = Path().apply {
        moveTo(cx - w * 0.12f, cy - h * 0.1f)
        lineTo(cx + w * 0.12f, cy - h * 0.1f)
        lineTo(cx + w * 0.22f, cy + h * 0.35f)
        lineTo(cx - w * 0.22f, cy + h * 0.35f)
        close()
    }
    drawPath(robePath, if (hit > 0.3f) hitTint else Color(0xFF311B92))

    // Gold robe borders & trim
    drawPath(robePath, Color(0xFFFFD700), style = Stroke(width = 4f))

    // Wizard Pointed Hat
    val hatBrim = Path().apply {
        moveTo(cx - w * 0.25f, cy - h * 0.18f)
        lineTo(cx + w * 0.25f, cy - h * 0.18f)
        lineTo(cx, cy - h * 0.22f)
        close()
    }
    drawPath(hatBrim, Color(0xFF4A148C))

    val hatCone = Path().apply {
        moveTo(cx - w * 0.18f, cy - h * 0.18f)
        quadraticTo(cx - w * 0.05f, cy - h * 0.38f, cx + w * 0.05f, cy - h * 0.48f)
        lineTo(cx + w * 0.15f, cy - h * 0.18f)
        close()
    }
    drawPath(hatCone, Color(0xFF4A148C))

    // Glowing face shadow & glowing cyan eyes
    drawRoundRect(
        color = Color(0xFF212121),
        topLeft = Offset(cx - w * 0.12f, cy - h * 0.18f),
        size = Size(w * 0.24f, h * 0.14f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(cx - w * 0.05f, cy - h * 0.11f))
    drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(cx + w * 0.05f, cy - h * 0.11f))

    // Arcane Magic Staff
    val staffX = cx + w * 0.22f
    val staffY = cy + h * 0.35f
    val staffTopY = cy - h * 0.35f
    drawLine(
        color = Color(0xFF8D6E63),
        start = Offset(staffX, staffY),
        end = Offset(staffX, staffTopY),
        strokeWidth = 8f
    )

    // Glowing Arcane Orb at top
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00FFFF), Color(0xFF7C4DFF), Color.Transparent),
            center = Offset(staffX, staffTopY),
            radius = w * 0.18f
        ),
        radius = w * 0.16f,
        center = Offset(staffX, staffTopY)
    )
    drawCircle(Color(0xFFE040FB), radius = w * 0.07f, center = Offset(staffX, staffTopY))

    // Magic spell blast when attacking
    if (attackProgress > 0.05f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFEA00), Color(0xFFFF3D00), Color.Transparent),
                center = Offset(staffX + attackProgress * w * 0.4f, staffTopY),
                radius = w * 0.2f
            ),
            radius = w * 0.18f,
            center = Offset(staffX + attackProgress * w * 0.4f, staffTopY)
        )
    }
}

private fun DrawScope.drawArcher(w: Float, h: Float, bounce: Float, lungeX: Float, attackProgress: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val hitTint = if (hit > 0.3f) Color.White else Color.Transparent

    // Ranger Quiver on back
    drawRoundRect(
        color = Color(0xFF5D4037),
        topLeft = Offset(cx - w * 0.22f, cy - h * 0.15f),
        size = Size(w * 0.08f, h * 0.28f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    // Arrow feathers
    drawLine(Color(0xFFECEFF1), Offset(cx - w * 0.2f, cy - h * 0.15f), Offset(cx - w * 0.22f, cy - h * 0.25f), 4f)
    drawLine(Color(0xFFFFD54F), Offset(cx - w * 0.18f, cy - h * 0.15f), Offset(cx - w * 0.16f, cy - h * 0.26f), 4f)

    // Green Leather Tunic
    drawRoundRect(
        color = if (hit > 0.3f) hitTint else Color(0xFF2E7D32),
        topLeft = Offset(cx - w * 0.15f, cy - h * 0.08f),
        size = Size(w * 0.3f, h * 0.32f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    // Leather belt
    drawRect(Color(0xFF3E2723), Offset(cx - w * 0.15f, cy + h * 0.08f), Size(w * 0.3f, h * 0.04f))

    // Archer Hood & Feather
    drawRoundRect(
        color = if (hit > 0.3f) hitTint else Color(0xFF1B5E20),
        topLeft = Offset(cx - w * 0.14f, cy - h * 0.3f),
        size = Size(w * 0.28f, h * 0.24f),
        cornerRadius = CornerRadius(16f, 16f)
    )
    // Feather on hood
    val featherPath = Path().apply {
        moveTo(cx + w * 0.08f, cy - h * 0.26f)
        quadraticTo(cx + w * 0.22f, cy - h * 0.38f, cx + w * 0.18f, cy - h * 0.44f)
        lineTo(cx + w * 0.1f, cy - h * 0.28f)
        close()
    }
    drawPath(featherPath, Color(0xFFFF1744))

    // Eyes
    drawCircle(Color(0xFF76FF03), radius = 4f, center = Offset(cx + w * 0.02f, cy - h * 0.15f))
    drawCircle(Color(0xFF76FF03), radius = 4f, center = Offset(cx + w * 0.1f, cy - h * 0.15f))

    // Recurve Longbow
    val bowCenterX = cx + w * 0.22f
    val bowCenterY = cy + h * 0.05f
    val bowPath = Path().apply {
        moveTo(bowCenterX - w * 0.05f, bowCenterY - h * 0.28f)
        quadraticTo(bowCenterX + w * 0.12f, bowCenterY, bowCenterX - w * 0.05f, bowCenterY + h * 0.28f)
    }
    drawPath(bowPath, Color(0xFF6D4C41), style = Stroke(width = 7f))

    // Bowstring
    drawLine(
        color = Color(0xFFEEEEEE),
        start = Offset(bowCenterX - w * 0.05f, bowCenterY - h * 0.28f),
        end = Offset(bowCenterX - attackProgress * 20f, bowCenterY),
        strokeWidth = 2f
    )
    drawLine(
        color = Color(0xFFEEEEEE),
        start = Offset(bowCenterX - attackProgress * 20f, bowCenterY),
        end = Offset(bowCenterX - w * 0.05f, bowCenterY + h * 0.28f),
        strokeWidth = 2f
    )

    // Flying Arrow when attacking
    if (attackProgress > 0.2f) {
        val arrowHeadX = bowCenterX + (attackProgress * w * 0.5f)
        drawLine(
            color = Color(0xFFFFD54F),
            start = Offset(arrowHeadX - w * 0.25f, bowCenterY),
            end = Offset(arrowHeadX, bowCenterY),
            strokeWidth = 5f
        )
        drawCircle(Color(0xFFFF1744), radius = 5f, center = Offset(arrowHeadX, bowCenterY))
    }
}

private fun DrawScope.drawAssassin(w: Float, h: Float, bounce: Float, lungeX: Float, attackProgress: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val hitTint = if (hit > 0.3f) Color.White else Color.Transparent

    // Shadow Aura Wisps
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x557C4DFF), Color.Transparent),
            center = Offset(cx, cy),
            radius = w * 0.4f
        ),
        radius = w * 0.38f,
        center = Offset(cx, cy)
    )

    // Dark Ninja Cloak
    val cloakPath = Path().apply {
        moveTo(cx - w * 0.14f, cy - h * 0.1f)
        lineTo(cx + w * 0.14f, cy - h * 0.1f)
        lineTo(cx + w * 0.18f, cy + h * 0.34f)
        lineTo(cx - w * 0.18f, cy + h * 0.34f)
        close()
    }
    drawPath(cloakPath, if (hit > 0.3f) hitTint else Color(0xFF212121))

    // Cowl & Mask
    drawRoundRect(
        color = if (hit > 0.3f) hitTint else Color(0xFF121212),
        topLeft = Offset(cx - w * 0.13f, cy - h * 0.3f),
        size = Size(w * 0.26f, h * 0.24f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    // Red glowing lethal eyes
    drawCircle(Color(0xFFFF1744), radius = 4.5f, center = Offset(cx - w * 0.05f, cy - h * 0.16f))
    drawCircle(Color(0xFFFF1744), radius = 4.5f, center = Offset(cx + w * 0.05f, cy - h * 0.16f))

    // Dual Shadow Daggers (left & right)
    val daggerL_X = cx - w * 0.2f
    val daggerL_Y = cy + h * 0.12f
    val daggerR_X = cx + w * 0.2f + (attackProgress * w * 0.15f)
    val daggerR_Y = cy + h * 0.05f

    // Dagger 1
    drawLine(Color(0xFFE040FB), Offset(daggerL_X, daggerL_Y), Offset(daggerL_X - w * 0.1f, daggerL_Y - h * 0.15f), 6f)
    // Dagger 2 (attacking blade)
    drawLine(Color(0xFF00E5FF), Offset(daggerR_X, daggerR_Y), Offset(daggerR_X + w * 0.18f, daggerR_Y - h * 0.12f), 7f)

    if (attackProgress > 0.1f) {
        // Cross slash effect
        drawLine(Color(0xFFFF1744), Offset(cx + w * 0.1f, cy - h * 0.2f), Offset(cx + w * 0.45f, cy + h * 0.15f), 6f)
        drawLine(Color(0xFFE040FB), Offset(cx + w * 0.45f, cy - h * 0.2f), Offset(cx + w * 0.1f, cy + h * 0.15f), 6f)
    }
}

private fun DrawScope.drawPaladin(w: Float, h: Float, bounce: Float, lungeX: Float, attackProgress: Float, hit: Float) {
    val cx = w * 0.5f + lungeX
    val cy = h * 0.5f + bounce
    val hitTint = if (hit > 0.3f) Color.White else Color.Transparent

    // Holy Radiant Halo
    drawCircle(
        color = Color(0xFFFFD700),
        radius = w * 0.18f,
        center = Offset(cx, cy - h * 0.28f),
        style = Stroke(width = 4f)
    )

    // Ivory & Gold Plate Body
    drawRoundRect(
        color = if (hit > 0.3f) hitTint else Color(0xFFECEFF1),
        topLeft = Offset(cx - w * 0.18f, cy - h * 0.08f),
        size = Size(w * 0.36f, h * 0.34f),
        cornerRadius = CornerRadius(16f, 16f)
    )
    // Golden Cross Emblem
    drawRect(Color(0xFFFFD700), Offset(cx - w * 0.025f, cy - h * 0.04f), Size(w * 0.05f, h * 0.2f))
    drawRect(Color(0xFFFFD700), Offset(cx - w * 0.1f, cy + h * 0.02f), Size(w * 0.2f, h * 0.05f))

    // Winged Helm
    drawRoundRect(
        color = if (hit > 0.3f) hitTint else Color(0xFFCFD8DC),
        topLeft = Offset(cx - w * 0.15f, cy - h * 0.32f),
        size = Size(w * 0.3f, h * 0.25f),
        cornerRadius = CornerRadius(16f, 16f)
    )
    // Golden Wings on helm
    val wingL = Path().apply {
        moveTo(cx - w * 0.15f, cy - h * 0.28f)
        lineTo(cx - w * 0.28f, cy - h * 0.42f)
        lineTo(cx - w * 0.12f, cy - h * 0.22f)
        close()
    }
    val wingR = Path().apply {
        moveTo(cx + w * 0.15f, cy - h * 0.28f)
        lineTo(cx + w * 0.28f, cy - h * 0.42f)
        lineTo(cx + w * 0.12f, cy - h * 0.22f)
        close()
    }
    drawPath(wingL, Color(0xFFFFD700))
    drawPath(wingR, Color(0xFFFFD700))

    // Warhammer of Justice
    val hammerBaseX = cx + w * 0.2f
    val hammerBaseY = cy + h * 0.15f
    val hammerHeadX = hammerBaseX + attackProgress * w * 0.2f
    val hammerHeadY = cy - h * 0.25f + attackProgress * h * 0.35f

    // Shaft
    drawLine(Color(0xFF8D6E63), Offset(hammerBaseX, hammerBaseY), Offset(hammerHeadX, hammerHeadY), 8f)
    // Golden Hammer Head
    drawRoundRect(
        color = Color(0xFFFFD700),
        topLeft = Offset(hammerHeadX - w * 0.1f, hammerHeadY - h * 0.08f),
        size = Size(w * 0.2f, h * 0.14f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Divine impact sparks
    if (attackProgress > 0.2f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD700), Color.Transparent),
                center = Offset(hammerHeadX + w * 0.1f, hammerHeadY),
                radius = w * 0.22f
            ),
            radius = w * 0.2f,
            center = Offset(hammerHeadX + w * 0.1f, hammerHeadY)
        )
    }
}
