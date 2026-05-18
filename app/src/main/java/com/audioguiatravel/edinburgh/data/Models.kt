package com.audioguiatravel.edinburgh.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityCatalog(
    val cityId: String,
    val cityName: String,
    val tours: List<Tour>,
)

@Serializable
data class Tour(
    val id: String,
    val title: String,
    val subtitle: String,
    val mood: TourMood,
    val narratorId: String,
    val narratorName: String,
    val narratorBio: String,
    val coverAsset: String,
    val accentColor: String,
    val estimatedMinutes: Int,
    val stopCount: Int,
    val description: String,
    val downloadSizeMb: Int = 120,
    val assetPackName: String = "tour_content",
    val stops: List<Stop>,
)

@Serializable
data class Stop(
    val id: String,
    val order: Int,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val audioAsset: String,
    val durationSeconds: Int,
    val historicalImageAsset: String,
    val summary: String,
    val geofenceRadiusMeters: Int = 45,
)

@Serializable
data class StopScript(
    val stopId: String,
    val title: String,
    val narration: String,
    val intro: String = "",
    val body: String = "",
    val outro: String = "",
    val sfxNotes: String = "",
    val imageCaption: String = "",
    val estimatedSpokenSeconds: Int = 0,
)

@Serializable
data class TourScriptPack(
    val tourId: String,
    val language: String,
    val stops: List<StopScript>,
)

@Serializable
enum class TourMood {
    @SerialName("CALM_HISTORIC")
    CALM_HISTORIC,

    @SerialName("CHEERFUL")
    CHEERFUL,

    @SerialName("SUSPENSE")
    SUSPENSE,

    @SerialName("ADVENTURE")
    ADVENTURE,
}
