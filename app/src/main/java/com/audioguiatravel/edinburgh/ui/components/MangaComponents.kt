package com.audioguiatravel.edinburgh.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.audioguiatravel.edinburgh.ui.theme.MangaBodyStyle
import com.audioguiatravel.edinburgh.ui.theme.MangaCaptionStyle
import com.audioguiatravel.edinburgh.ui.theme.MangaInk
import com.audioguiatravel.edinburgh.ui.theme.MangaPaper
import com.audioguiatravel.edinburgh.ui.theme.MangaShadow
import com.audioguiatravel.edinburgh.ui.theme.MangaSpeechFill
import com.audioguiatravel.edinburgh.ui.theme.MangaSpeedLine

@Composable
fun MangaPanel(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 3.dp,
    accent: Color = MangaInk,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MangaPaper)
            .border(borderWidth, accent, RoundedCornerShape(4.dp))
            .padding(16.dp),
        content = content,
    )
}

@Composable
fun SpeechBubble(
    speaker: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = speaker.uppercase(),
            style = MangaCaptionStyle,
            color = MaterialTheme.colorScheme.secondary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MangaSpeechFill)
                .border(2.dp, MangaInk, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(text = text, style = MangaBodyStyle, color = MangaInk)
        }
    }
}

@Composable
fun HalftoneBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val step = 14f
        var y = 0f
        while (y < size.height) {
            var x = 0f
            while (x < size.width) {
                drawCircle(
                    color = MangaSpeedLine,
                    radius = 1.8f,
                    center = Offset(x, y),
                )
                x += step
            }
            y += step
        }
    }
}

@Composable
fun SpeedLinesAccent(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 2f)
        val center = Offset(size.width * 0.2f, size.height * 0.3f)
        repeat(8) { i ->
            val angle = -0.4f + i * 0.12f
            val end = Offset(
                center.x + size.width * 1.2f * kotlin.math.cos(angle),
                center.y + size.height * 0.8f * kotlin.math.sin(angle),
            )
            drawLine(MangaShadow, center, end, strokeWidth = stroke.width)
        }
    }
}

@Composable
fun MangaTailPointer(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width / 2, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, MangaSpeechFill)
        drawPath(path, MangaInk, style = Stroke(width = 2f))
    }
}
