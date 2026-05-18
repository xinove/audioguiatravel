package com.audioguiatravel.edinburgh.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.audioguiatravel.edinburgh.ui.theme.MangaAccentPink
import com.audioguiatravel.edinburgh.ui.theme.MangaAccentTeal
import com.audioguiatravel.edinburgh.ui.theme.MangaInk
import com.audioguiatravel.edinburgh.ui.theme.MangaPaper

@Composable
fun MangaNarratorAvatar(
    narratorId: String,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    accent: Color = MangaAccentPink,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.028f)

        drawCircle(MangaPaper, radius = w * 0.48f, center = Offset(w / 2, h / 2))
        drawCircle(MangaInk, radius = w * 0.48f, center = Offset(w / 2, h / 2), style = stroke)

        when (narratorId) {
            "james_stuart" -> drawJames(w, h, accent, stroke)
            "margaret_reid" -> drawMargaret(w, h, accent, stroke)
            "ghost_warden" -> drawGhostWarden(w, h, accent, stroke)
            "fiona_macleod" -> drawFiona(w, h, accent, stroke)
            else -> drawJames(w, h, accent, stroke)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawJames(
    w: Float,
    h: Float,
    accent: Color,
    stroke: Stroke,
) {
    val cx = w / 2
    val cy = h * 0.42f
    drawCircle(accent.copy(alpha = 0.25f), w * 0.22f, Offset(cx, cy))
    drawArc(
        color = MangaInk,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(cx - w * 0.28f, cy - h * 0.05f),
        size = Size(w * 0.56f, h * 0.5f),
        style = stroke,
    )
    drawLine(MangaInk, Offset(cx - w * 0.12f, cy + h * 0.08f), Offset(cx + w * 0.12f, cy + h * 0.08f), stroke.width)
    drawCircle(MangaInk, w * 0.03f, Offset(cx - w * 0.08f, cy))
    drawCircle(MangaInk, w * 0.03f, Offset(cx + w * 0.08f, cy))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMargaret(
    w: Float,
    h: Float,
    accent: Color,
    stroke: Stroke,
) {
    val cx = w / 2
    val cy = h * 0.4f
    drawCircle(MangaAccentTeal.copy(alpha = 0.35f), w * 0.24f, Offset(cx, cy - h * 0.06f))
    val hair = Path().apply {
        moveTo(cx - w * 0.3f, cy)
        quadraticTo(cx, cy - h * 0.22f, cx + w * 0.3f, cy)
        lineTo(cx + w * 0.26f, cy + h * 0.18f)
        quadraticTo(cx, cy + h * 0.08f, cx - w * 0.26f, cy + h * 0.18f)
        close()
    }
    drawPath(hair, accent)
    drawPath(hair, MangaInk, style = stroke)
    drawCircle(MangaInk, w * 0.035f, Offset(cx - w * 0.09f, cy + h * 0.04f))
    drawCircle(MangaInk, w * 0.035f, Offset(cx + w * 0.09f, cy + h * 0.04f))
    drawArc(
        MangaInk,
        10f,
        160f,
        false,
        Offset(cx - w * 0.08f, cy + h * 0.1f),
        Size(w * 0.16f, h * 0.08f),
        style = stroke,
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGhostWarden(
    w: Float,
    h: Float,
    accent: Color,
    stroke: Stroke,
) {
    val cx = w / 2
    val cy = h * 0.45f
    drawCircle(accent.copy(alpha = 0.5f), w * 0.2f, Offset(cx, cy))
    drawLine(
        MangaInk,
        Offset(cx - w * 0.18f, cy - h * 0.12f),
        Offset(cx + w * 0.18f, cy - h * 0.12f),
        stroke.width * 1.4f,
    )
    drawCircle(Color.White, w * 0.05f, Offset(cx - w * 0.08f, cy))
    drawCircle(Color.White, w * 0.05f, Offset(cx + w * 0.08f, cy))
    drawCircle(MangaInk, w * 0.02f, Offset(cx - w * 0.08f, cy))
    drawCircle(MangaInk, w * 0.02f, Offset(cx + w * 0.08f, cy))
    val cloak = Path().apply {
        moveTo(cx - w * 0.32f, cy + h * 0.05f)
        lineTo(cx + w * 0.32f, cy + h * 0.05f)
        lineTo(cx + w * 0.2f, cy + h * 0.35f)
        lineTo(cx - w * 0.2f, cy + h * 0.35f)
        close()
    }
    drawPath(cloak, MangaInk.copy(alpha = 0.85f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFiona(
    w: Float,
    h: Float,
    accent: Color,
    stroke: Stroke,
) {
    val cx = w / 2
    val cy = h * 0.42f
    drawLine(
        accent,
        Offset(cx, cy - h * 0.2f),
        Offset(cx, cy + h * 0.25f),
        stroke.width * 2.2f,
    )
    drawCircle(accent.copy(alpha = 0.3f), w * 0.2f, Offset(cx, cy))
    drawPath(
        Path().apply {
            moveTo(cx - w * 0.22f, cy - h * 0.02f)
            lineTo(cx, cy - h * 0.14f)
            lineTo(cx + w * 0.22f, cy - h * 0.02f)
        },
        MangaInk,
        style = stroke,
    )
    drawCircle(MangaInk, w * 0.03f, Offset(cx - w * 0.07f, cy + h * 0.02f))
    drawCircle(MangaInk, w * 0.03f, Offset(cx + w * 0.07f, cy + h * 0.02f))
}
