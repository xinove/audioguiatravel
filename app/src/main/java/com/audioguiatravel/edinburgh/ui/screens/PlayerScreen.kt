package com.audioguiatravel.edinburgh.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.audioguiatravel.edinburgh.audio.PlaybackUiState
import com.audioguiatravel.edinburgh.data.Stop
import com.audioguiatravel.edinburgh.data.StopScript
import com.audioguiatravel.edinburgh.data.Tour
import com.audioguiatravel.edinburgh.location.ProximityState
import com.audioguiatravel.edinburgh.ui.components.MangaNarratorAvatar
import com.audioguiatravel.edinburgh.ui.components.MangaPanel
import com.audioguiatravel.edinburgh.ui.components.SpeechBubble
import com.audioguiatravel.edinburgh.ui.components.StopLocationView
import com.audioguiatravel.edinburgh.ui.theme.AppEmoji
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    tour: Tour,
    stop: Stop,
    playback: StateFlow<PlaybackUiState>,
    proximity: StateFlow<ProximityState>,
    script: StopScript?,
    onBeginTracking: () -> Unit,
    onEndTracking: () -> Unit,
    onBack: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekBy: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSelectStop: (Stop) -> Unit,
    onArrivalPlay: (Stop) -> Unit,
    onDismissArrival: () -> Unit,
    onCyclePlaybackSpeed: () -> Unit,
    imageUriForAsset: (String) -> String,
) {
    val state by playback.collectAsState()
    val prox by proximity.collectAsState()
    val activeStop = tour.stops.find { it.id == state.stopId } ?: stop
    val accent = runCatching { Color(android.graphics.Color.parseColor(tour.accentColor)) }
        .getOrElse { MaterialTheme.colorScheme.primary }

    var selectedTab by remember { mutableIntStateOf(0) }

    DisposableEffect(tour.id) {
        onBeginTracking()
        onDispose { onEndTracking() }
    }

    prox.arrivedStop?.let { arrived ->
        AlertDialog(
            onDismissRequest = onDismissArrival,
            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            title = { Text("${AppEmoji.PIN} ¡Has llegado a la parada ${arrived.order}!") },
            text = { Text(arrived.title) },
            confirmButton = {
                FilledTonalButton(onClick = { onArrivalPlay(arrived) }) {
                    Text("${AppEmoji.PLAY} Reproducir")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissArrival) { Text("Ahora no 🙈") }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${AppEmoji.tourMood(tour.mood)} Parada ${activeStop.order}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("${AppEmoji.HEADPHONES} Audio") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("${AppEmoji.PIN} Ubicación") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("${AppEmoji.SCRIPT} Guión") })
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> AudioTab(
                        tour = tour,
                        activeStop = activeStop,
                        state = state,
                        prox = prox,
                        accent = accent,
                        modifier = Modifier.fillMaxSize(),
                        onTogglePlayPause = onTogglePlayPause,
                        onSeek = onSeek,
                        onSeekBy = onSeekBy,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        onSelectStop = onSelectStop,
                        onCyclePlaybackSpeed = onCyclePlaybackSpeed,
                        imageUriForAsset = imageUriForAsset,
                    )

                    1 -> StopLocationView(
                        stop = activeStop,
                        proximity = prox,
                        accent = accent,
                        imageUriForAsset = imageUriForAsset,
                        modifier = Modifier.fillMaxSize(),
                    )

                    2 -> ScriptTab(
                        script = script,
                        tour = tour,
                        accent = accent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioTab(
    tour: Tour,
    activeStop: Stop,
    state: PlaybackUiState,
    prox: ProximityState,
    accent: Color,
    modifier: Modifier = Modifier,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekBy: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSelectStop: (Stop) -> Unit,
    onCyclePlaybackSpeed: () -> Unit,
    imageUriForAsset: (String) -> String,
) {
    val durationMs = (state.durationMs.takeIf { it > 0 } ?: activeStop.durationSeconds * 1000L).toFloat()
    var sliderPosition by remember(activeStop.id) { mutableFloatStateOf(0f) }
    val positionFraction = if (durationMs > 0f) (state.positionMs / durationMs).coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MangaNarratorAvatar(narratorId = tour.narratorId, accent = accent, size = 72.dp)
            Column {
                Text(activeStop.title, style = MaterialTheme.typography.titleLarge)
                prox.distanceToNearestMeters?.let { d ->
                    Text(
                        "${AppEmoji.PIN} A ~${d.toInt()} m de la parada más cercana",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }

        HistoricalImage(
            assetPath = activeStop.historicalImageAsset,
            imageUri = imageUriForAsset(activeStop.historicalImageAsset),
        )

        SpeechBubble(speaker = tour.narratorName, text = activeStop.summary)

        state.errorMessage?.let { message ->
            MangaPanel(accent = MaterialTheme.colorScheme.error) {
                Text(
                    text = "🔇 $message",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        MangaPanel(accent = accent) {
            if (state.isBuffering) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            }
            Slider(
                value = sliderPosition.takeIf { it > 0f } ?: positionFraction,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = {
                    onSeek((sliderPosition * durationMs).toLong())
                    sliderPosition = 0f
                },
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatMs(state.positionMs), style = MaterialTheme.typography.labelSmall)
                Text(formatMs(durationMs.toLong()), style = MaterialTheme.typography.labelSmall)
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior")
                }
                IconButton(onClick = { onSeekBy(-10_000) }) {
                    Icon(Icons.Default.Replay10, contentDescription = "-10s")
                }
                FilledIconButton(onClick = onTogglePlayPause, modifier = Modifier.height(56.dp)) {
                    Icon(
                        if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pausa",
                    )
                }
                IconButton(onClick = { onSeekBy(10_000) }) {
                    Icon(Icons.Default.Forward10, contentDescription = "+10s")
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Siguiente")
                }
            }
            FilledTonalButton(
                onClick = onCyclePlaybackSpeed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Text(
                    text = "Velocidad: ${AppEmoji.speedLabel(state.playbackSpeed)}",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = "Toca para cambiar: 1x → 1.25x → 1.5x → 1.75x → 2x ${AppEmoji.RABBIT}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Text(
            text = "${AppEmoji.WALK} Otras paradas",
            style = MaterialTheme.typography.titleSmall,
        )

        tour.stops.sortedBy { it.order }.forEach { item ->
            val selected = item.id == activeStop.id
            MangaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (!selected) Modifier.clickable { onSelectStop(item) } else Modifier),
                accent = if (selected) accent else MaterialTheme.colorScheme.outline,
            ) {
                Text("${item.order}. ${item.title}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ScriptTab(
    script: StopScript?,
    tour: Tour,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (script == null) {
            Text("${AppEmoji.BOOK} Guión no disponible para esta parada.")
            return
        }
        MangaPanel(accent = accent) {
            Text(
                "${AppEmoji.SCRIPT} Duración hablada ~${script.estimatedSpokenSeconds / 60} min",
                style = MaterialTheme.typography.labelMedium,
            )
            if (script.imageCaption.isNotBlank()) {
                Text(script.imageCaption, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
        if (script.intro.isNotBlank()) {
            MangaPanel(accent = accent) {
                Text("Entrada", style = MaterialTheme.typography.labelLarge, color = accent)
                Text(script.intro, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 6.dp))
            }
        }
        if (script.body.isNotBlank()) {
            MangaPanel(accent = MaterialTheme.colorScheme.outline) {
                Text("Historia", style = MaterialTheme.typography.labelLarge)
                Text(script.body, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 6.dp))
            }
        } else {
            Text(script.narration, style = MaterialTheme.typography.bodyLarge)
        }
        if (script.outro.isNotBlank()) {
            MangaPanel(accent = accent) {
                Text("Salida / siguiente parada", style = MaterialTheme.typography.labelLarge, color = accent)
                Text(script.outro, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
            }
        }
        if (script.sfxNotes.isNotBlank()) {
            MangaPanel(accent = MaterialTheme.colorScheme.tertiary) {
                Text("${AppEmoji.GHOST} Notas de producción / SFX", style = MaterialTheme.typography.labelLarge)
                Text(script.sfxNotes, modifier = Modifier.padding(top = 6.dp))
            }
        }
        SpeechBubble(speaker = tour.narratorName, text = "Lee con la pausa de quien recuerda, no de quien presume.")
    }
}

@Composable
private fun HistoricalImage(assetPath: String, imageUri: String) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .build(),
        contentDescription = "Ilustración histórica",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(MaterialTheme.shapes.medium),
        contentScale = ContentScale.Crop,
    )
}

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
