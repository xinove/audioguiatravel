package com.audioguiatravel.edinburgh.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.audioguiatravel.edinburgh.data.Stop
import com.audioguiatravel.edinburgh.data.Tour
import com.audioguiatravel.edinburgh.download.TourDownloadState
import com.audioguiatravel.edinburgh.ui.components.DownloadTourButton
import com.audioguiatravel.edinburgh.ui.components.MangaNarratorAvatar
import com.audioguiatravel.edinburgh.ui.components.MangaPanel
import com.audioguiatravel.edinburgh.ui.components.SpeechBubble
import com.audioguiatravel.edinburgh.ui.theme.AppEmoji
import com.audioguiatravel.edinburgh.ui.theme.MangaInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourDetailScreen(
    tour: Tour,
    downloadState: TourDownloadState,
    onDownload: () -> Unit,
    onBack: () -> Unit,
    onStopClick: (Stop) -> Unit,
) {
    val accent = runCatching { Color(android.graphics.Color.parseColor(tour.accentColor)) }
        .getOrElse { MangaInk }
    val orderedStops = tour.stops.sortedBy { it.order }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${AppEmoji.tourMood(tour.mood)} ${tour.title}", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MangaNarratorAvatar(
                        narratorId = tour.narratorId,
                        accent = accent,
                        size = 88.dp,
                        modifier = Modifier.padding(end = 16.dp),
                    )
                    SpeechBubble(
                        speaker = tour.narratorName,
                        text = tour.narratorBio,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                MangaPanel(accent = accent) {
                    Text(tour.description, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${AppEmoji.WALK} ${tour.stopCount} paradas · ~${tour.estimatedMinutes} min",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp),
                        color = accent,
                    )
                }
            }

            item {
                DownloadTourButton(
                    tour = tour,
                    state = downloadState,
                    onDownload = onDownload,
                )
            }

            item {
                Text(
                    text = "${AppEmoji.PIN} PARADAS",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            items(orderedStops, key = { it.id }) { stop ->
                StopRow(
                    stop = stop,
                    accent = accent,
                    onClick = { onStopClick(stop) },
                )
            }
        }
    }
}

@Composable
private fun StopRow(stop: Stop, accent: Color, onClick: () -> Unit) {
    MangaPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = accent,
    ) {
        Text(
            text = "${AppEmoji.HEADPHONES} Parada ${stop.order}",
            style = MaterialTheme.typography.labelMedium,
            color = accent,
        )
        Text(
            text = stop.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = stop.summary,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = formatDuration(stop.durationSeconds),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d aprox.".format(m, s)
}
