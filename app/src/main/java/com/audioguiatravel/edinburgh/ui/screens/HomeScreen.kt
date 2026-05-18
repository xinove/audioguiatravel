package com.audioguiatravel.edinburgh.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.audioguiatravel.edinburgh.data.Tour
import com.audioguiatravel.edinburgh.data.TourMood
import com.audioguiatravel.edinburgh.download.TourDownloadState
import com.audioguiatravel.edinburgh.ui.components.DownloadTourButton
import com.audioguiatravel.edinburgh.ui.components.HalftoneBackground
import com.audioguiatravel.edinburgh.ui.components.MangaNarratorAvatar
import com.audioguiatravel.edinburgh.ui.components.MangaPanel
import com.audioguiatravel.edinburgh.ui.components.SpeedLinesAccent
import com.audioguiatravel.edinburgh.ui.theme.MangaAccentPink
import com.audioguiatravel.edinburgh.ui.theme.MangaInk
import com.audioguiatravel.edinburgh.ui.theme.AppEmoji
import com.audioguiatravel.edinburgh.ui.theme.MangaTitleStyle

@Composable
fun HomeScreen(
    cityName: String,
    tours: List<Tour>,
    downloadStates: Map<String, TourDownloadState>,
    downloadStateFor: (Tour) -> TourDownloadState,
    onDownloadTour: (Tour) -> Unit,
    onTourClick: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HalftoneBackground(modifier = Modifier.fillMaxSize())
        SpeedLinesAccent(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp),
        ) {
            Text(
                text = "${AppEmoji.HEADPHONES} AUDIOGUÍAS ${AppEmoji.SPARKLES}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = "${AppEmoji.CASTLE} $cityName",
                style = MangaTitleStyle,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
            Text(
                text = "${AppEmoji.WALK} Elige tu capítulo. Camina, pausa y escucha.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            DownloadTourButton(
                tour = tours.first(),
                state = downloadStates["all"] ?: downloadStateFor(tours.first()),
                onDownload = { onDownloadTour(tours.first()) },
                modifier = Modifier.padding(bottom = 16.dp),
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                items(tours, key = { it.id }) { tour ->
                    TourCard(tour = tour, onClick = { onTourClick(tour.id) })
                }
            }
        }
    }
}

@Composable
private fun TourCard(tour: Tour, onClick: () -> Unit) {
    val accent = parseAccent(tour.accentColor)
    val moodEmoji = AppEmoji.tourMood(tour.mood)
    val moodLabel = when (tour.mood) {
        TourMood.CALM_HISTORIC -> "$moodEmoji Histórico · calmado"
        TourMood.CHEERFUL -> "$moodEmoji Alegre · Ilustración"
        TourMood.SUSPENSE -> "$moodEmoji Misterio · SFX"
        TourMood.ADVENTURE -> "$moodEmoji Aventura · colina"
    }

    MangaPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = accent,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MangaNarratorAvatar(
                narratorId = tour.narratorId,
                accent = accent,
                size = 56.dp,
                modifier = Modifier.padding(end = 12.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = moodLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                )
                Text(
                    text = "${AppEmoji.BOOK} ${tour.title}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MangaInk,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = tour.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Text(
            text = tour.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MangaInk,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

private fun parseAccent(hex: String): Color {
    return runCatching {
        Color(android.graphics.Color.parseColor(hex))
    }.getOrElse { MangaAccentPink }
}
