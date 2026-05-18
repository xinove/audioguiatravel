package com.audioguiatravel.edinburgh.download

import android.content.Context
import com.audioguiatravel.edinburgh.data.Tour
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DownloadStatus {
    NOT_STARTED,
    DOWNLOADING,
    READY,
    FAILED,
}

data class TourDownloadState(
    val status: DownloadStatus = DownloadStatus.NOT_STARTED,
    val progressPercent: Int = 0,
    val bytesDownloaded: Long = 0,
    val totalBytes: Long = 0,
)

class TourDownloadManager(context: Context) {

    private val appContext = context.applicationContext
    private val assetPackManager = AssetPackManagerFactory.getInstance(appContext)

    private val _states = MutableStateFlow<Map<String, TourDownloadState>>(emptyMap())
    val states: StateFlow<Map<String, TourDownloadState>> = _states.asStateFlow()

    private val packListener = AssetPackStateUpdateListener { state ->
        onPackState(state)
    }

    init {
        assetPackManager.registerListener(packListener)
        refreshInstalledState()
    }

    fun refreshInstalledState() {
        assetPackManager.getPackStates(listOf(ASSET_PACK_NAME))
            .addOnSuccessListener { bundle ->
                bundle.packStates().values.forEach { onPackState(it) }
            }
    }

    fun requestDownload(tour: Tour) {
        val packName = tour.assetPackName
        val current = _states.value[tour.id]?.status
        if (current == DownloadStatus.DOWNLOADING || current == DownloadStatus.READY) return

        _states.value = _states.value + (tour.id to TourDownloadState(DownloadStatus.DOWNLOADING, 0))

        assetPackManager.fetch(listOf(packName))
            .addOnSuccessListener {
                refreshInstalledState()
            }
            .addOnFailureListener {
                _states.value = _states.value + (
                    tour.id to TourDownloadState(
                        status = DownloadStatus.FAILED,
                        progressPercent = 0,
                    )
                    )
            }
    }

    fun isContentReady(tour: Tour): Boolean {
        return _states.value[tour.id]?.status == DownloadStatus.READY
    }

    fun getPackAssetsPath(packName: String): String? {
        return runCatching {
            assetPackManager.getPackLocation(packName)?.assetsPath()
        }.getOrNull()
    }

    private fun onPackState(state: AssetPackState) {
        val tourId = packToTourId(state.name()) ?: return
        val mapped = when (state.status()) {
            AssetPackStatus.DOWNLOADING,
            AssetPackStatus.TRANSFERRING,
            -> TourDownloadState(
                status = DownloadStatus.DOWNLOADING,
                progressPercent = (state.bytesDownloaded() * 100 / state.totalBytesToDownload().coerceAtLeast(1))
                    .toInt()
                    .coerceIn(0, 99),
                bytesDownloaded = state.bytesDownloaded(),
                totalBytes = state.totalBytesToDownload(),
            )

            AssetPackStatus.COMPLETED -> TourDownloadState(
                status = DownloadStatus.READY,
                progressPercent = 100,
                bytesDownloaded = state.totalBytesToDownload(),
                totalBytes = state.totalBytesToDownload(),
            )

            AssetPackStatus.FAILED,
            AssetPackStatus.CANCELED,
            AssetPackStatus.NOT_INSTALLED,
            -> TourDownloadState(status = DownloadStatus.FAILED)

            else -> _states.value[tourId] ?: TourDownloadState(DownloadStatus.NOT_STARTED)
        }
        _states.value = _states.value + (tourId to mapped)
    }

    private fun packToTourId(packName: String): String? {
        return when (packName) {
            ASSET_PACK_NAME -> "all"
            else -> null
        }
    }

    fun downloadStateFor(@Suppress("UNUSED_PARAMETER") tour: Tour): TourDownloadState {
        return _states.value["all"] ?: TourDownloadState()
    }

    fun isPackReady(): Boolean = _states.value["all"]?.status == DownloadStatus.READY

    companion object {
        const val ASSET_PACK_NAME = "tour_content"
    }
}
