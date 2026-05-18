package com.audioguiatravel.edinburgh.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import com.audioguiatravel.edinburgh.data.Tour
import com.audioguiatravel.edinburgh.download.DownloadStatus
import com.audioguiatravel.edinburgh.download.TourDownloadState
import com.audioguiatravel.edinburgh.ui.theme.AppEmoji

@Composable
fun DownloadTourButton(
    tour: Tour,
    state: TourDownloadState,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when (state.status) {
            DownloadStatus.NOT_STARTED -> {
                OutlinedButton(onClick = onDownload, modifier = Modifier.fillMaxWidth()) {
                    Text("${AppEmoji.DOWNLOAD} Descargar audio e imágenes (~${tour.downloadSizeMb} MB)")
                }
            }

            DownloadStatus.DOWNLOADING -> {
                LinearProgressIndicator(
                    progress = { state.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                )
                Text("${AppEmoji.SPARKLES} Descargando pack de Edimburgo… ${state.progressPercent}%")
            }

            DownloadStatus.READY -> {
                Text("${AppEmoji.READY} Contenido listo para escuchar sin Wi‑Fi.")
            }

            DownloadStatus.FAILED -> {
                OutlinedButton(onClick = onDownload, modifier = Modifier.fillMaxWidth()) {
                    Text("${AppEmoji.DOWNLOAD} Reintentar descarga")
                }
                Text(
                    "En desarrollo local, copia MP3 en assets o instala desde Play Internal Testing.",
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
