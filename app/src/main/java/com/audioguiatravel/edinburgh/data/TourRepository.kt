package com.audioguiatravel.edinburgh.data

import android.content.Context
import com.audioguiatravel.edinburgh.download.TourDownloadManager
import kotlinx.serialization.json.Json
import java.io.File

class TourRepository(
    context: Context,
    private val downloadManager: TourDownloadManager? = null,
) {

    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val catalog: CityCatalog by lazy {
        val raw = appContext.assets.open("tours/edinburgh.json")
            .bufferedReader()
            .use { it.readText() }
        json.decodeFromString<CityCatalog>(raw)
    }

    fun getCityName(): String = catalog.cityName

    fun getTours(): List<Tour> = catalog.tours

    fun getTour(tourId: String): Tour? = catalog.tours.find { it.id == tourId }

    fun assetUri(tour: Tour, assetPath: String): String {
        val packPath = downloadManager?.getPackAssetsPath(tour.assetPackName)
        if (packPath != null) {
            val file = File(packPath, assetPath)
            if (file.exists()) {
                return file.toURI().toString()
            }
        }
        return "asset:///$assetPath"
    }

    fun assetUri(assetPath: String): String = "asset:///$assetPath"

    fun hasBundledAudio(assetPath: String): Boolean {
        return runCatching {
            appContext.assets.open(assetPath).close()
            true
        }.getOrDefault(false)
    }

    fun missingAudioHint(assetPath: String): String {
        return "Falta el archivo de audio.\n\nCopia tu MP3 en:\napp/src/main/assets/$assetPath\n\nLuego: Build → Rebuild Project y vuelve a instalar la app."
    }
}
