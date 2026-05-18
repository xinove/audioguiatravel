package com.audioguiatravel.edinburgh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.audioguiatravel.edinburgh.data.Stop
import com.audioguiatravel.edinburgh.location.ProximityState
import com.audioguiatravel.edinburgh.ui.theme.AppEmoji

@Composable
fun StopLocationView(
    stop: Stop,
    proximity: ProximityState,
    accent: Color,
    imageUriForAsset: (String) -> String,
    modifier: Modifier = Modifier,
) {
    val imageUri = imageUriForAsset(stop.historicalImageAsset)
    val atStop = proximity.distanceToNearestMeters?.let { it <= stop.geofenceRadiusMeters } == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        MangaPanel(accent = accent) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = accent,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${AppEmoji.PIN} Escucha el audio aquí",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Parada ${stop.order} · ${stop.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        val context = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = "Ubicación de la parada ${stop.order}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(MaterialTheme.shapes.large),
            contentScale = ContentScale.Crop,
        )

        MangaPanel(accent = if (atStop) MaterialTheme.colorScheme.tertiary else accent) {
            val distanceLine = when {
                atStop -> "${AppEmoji.READY} Estás en el punto. Pulsa reproducir en la pestaña Audio."
                proximity.distanceToNearestMeters != null -> {
                    val meters = proximity.distanceToNearestMeters!!.toInt()
                    "${AppEmoji.WALK} Te faltan unos $meters m (radio ~${stop.geofenceRadiusMeters} m)."
                }
                !proximity.hasLocationPermission -> "Activa la ubicación para avisarte al llegar."
                else -> "${AppEmoji.WALK} Camina hasta el punto señalado en la imagen."
            }
            Text(distanceLine, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stop.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Text(
            text = "Coordenadas: ${formatCoord(stop.latitude)}, ${formatCoord(stop.longitude)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

private fun formatCoord(value: Double): String = "%.4f".format(value)
