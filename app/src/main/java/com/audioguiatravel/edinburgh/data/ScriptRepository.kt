package com.audioguiatravel.edinburgh.data

import android.content.Context
import kotlinx.serialization.json.Json

class ScriptRepository(context: Context) {

    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val cache = mutableMapOf<String, TourScriptPack>()

    fun getScript(tourId: String, stopId: String): StopScript? {
        val pack = getPack(tourId) ?: return null
        return pack.stops.find { it.stopId == stopId }
    }

    fun getPack(tourId: String): TourScriptPack? {
        cache[tourId]?.let { return it }
        val assetName = when (tourId) {
            "oldtown-historic" -> "scripts/oldtown-historic.json"
            "newtown-cheerful" -> "scripts/newtown-cheerful.json"
            "oldtown-ghosts" -> "scripts/oldtown-ghosts.json"
            "arthur-holyrood" -> "scripts/arthur-holyrood.json"
            else -> return null
        }
        return runCatching {
            val raw = appContext.assets.open(assetName).bufferedReader().use { it.readText() }
            json.decodeFromString<TourScriptPack>(raw).also { cache[tourId] = it }
        }.getOrNull()
    }
}
