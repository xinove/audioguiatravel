package com.audioguiatravel.edinburgh.ui.theme

import com.audioguiatravel.edinburgh.data.TourMood

object AppEmoji {
    const val CASTLE = "🏰"
    const val HEADPHONES = "🎧"
    const val MAP = "🗺️"
    const val SCRIPT = "📜"
    const val WALK = "🚶"
    const val SPARKLES = "✨"
    const val DOWNLOAD = "📥"
    const val READY = "✅"
    const val PIN = "📍"
    const val FAST = "⚡"
    const val RABBIT = "🐇"
    const val GHOST = "👻"
    const val TEA = "☕"
    const val MOUNTAIN = "⛰️"
    const val BOOK = "📖"
    const val PLAY = "▶️"
    const val PAUSE = "⏸️"

    fun tourMood(mood: TourMood): String = when (mood) {
        TourMood.CALM_HISTORIC -> "🕯️"
        TourMood.CHEERFUL -> "🌸"
        TourMood.SUSPENSE -> "👻"
        TourMood.ADVENTURE -> "⛰️"
    }

    fun speedLabel(speed: Float): String = when {
        speed >= 2f -> "$RABBIT ${formatSpeed(speed)}"
        speed > 1f -> "$FAST ${formatSpeed(speed)}"
        else -> formatSpeed(speed)
    }

    fun formatSpeed(speed: Float): String {
        return if (speed == speed.toLong().toFloat()) {
            "${speed.toLong()}x"
        } else {
            "${speed}x"
        }
    }
}
