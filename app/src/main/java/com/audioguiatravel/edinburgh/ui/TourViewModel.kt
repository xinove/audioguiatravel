package com.audioguiatravel.edinburgh.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.audioguiatravel.edinburgh.audio.PlaybackUiState
import com.audioguiatravel.edinburgh.audio.TourAudioController
import com.audioguiatravel.edinburgh.data.ScriptRepository
import com.audioguiatravel.edinburgh.data.Stop
import com.audioguiatravel.edinburgh.data.StopScript
import com.audioguiatravel.edinburgh.data.Tour
import com.audioguiatravel.edinburgh.data.TourRepository
import com.audioguiatravel.edinburgh.download.TourDownloadManager
import com.audioguiatravel.edinburgh.download.TourDownloadState
import com.audioguiatravel.edinburgh.location.LocationMonitor
import com.audioguiatravel.edinburgh.location.ProximityState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TourViewModel(application: Application) : AndroidViewModel(application) {

    private val downloadManager = TourDownloadManager(application)
    private val repository = TourRepository(application, downloadManager)
    private val scriptRepository = ScriptRepository(application)
    private val audio = TourAudioController(application, repository)
    private val locationMonitor = LocationMonitor(application)

    val cityName: String get() = repository.getCityName()
    val tours: List<Tour> = repository.getTours()

    val playback: StateFlow<PlaybackUiState> = audio.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlaybackUiState(),
    )

    val proximity: StateFlow<ProximityState> = locationMonitor.proximity.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProximityState(),
    )

    val downloadStates: StateFlow<Map<String, TourDownloadState>> = downloadManager.states

    private val _activeTourId = MutableStateFlow<String?>(null)
    val activeTourId: StateFlow<String?> = _activeTourId.asStateFlow()

    private var tickerJob: Job? = null

    init {
        downloadManager.refreshInstalledState()
    }

    fun getTour(tourId: String): Tour? = repository.getTour(tourId)

    fun assetUri(tour: Tour, assetPath: String): String = repository.assetUri(tour, assetPath)

    fun getScript(tourId: String, stopId: String): StopScript? =
        scriptRepository.getScript(tourId, stopId)

    fun downloadStateFor(tour: Tour): TourDownloadState = downloadManager.downloadStateFor(tour)

    fun requestTourDownload(tour: Tour) = downloadManager.requestDownload(tour)

    fun isAudioPackReady(): Boolean = downloadManager.isPackReady()

    fun setLocationPermission(granted: Boolean) {
        locationMonitor.setPermissionGranted(granted)
        if (granted) {
            locationMonitor.start()
        }
    }

    fun beginTourTracking(tour: Tour) {
        _activeTourId.value = tour.id
        locationMonitor.setActiveStops(tour.stops)
        if (proximity.value.hasLocationPermission) {
            locationMonitor.start()
        }
    }

    fun endTourTracking() {
        locationMonitor.stop()
        _activeTourId.value = null
    }

    fun acknowledgeArrival() {
        proximity.value.arrivedStop?.id?.let { locationMonitor.acknowledgeArrival(it) }
    }

    fun playArrivedStop(tour: Tour, stop: Stop) {
        acknowledgeArrival()
        selectStop(tour, stop)
        play()
    }

    fun selectStop(tour: Tour, stop: Stop) {
        audio.loadStop(tour, stop)
        startTicker()
    }

    fun togglePlayPause() = audio.togglePlayPause()

    fun play() = audio.play()

    fun pause() = audio.pause()

    fun seekTo(positionMs: Long) = audio.seekTo(positionMs)

    fun seekBy(deltaMs: Long) = audio.seekBy(deltaMs)

    fun cyclePlaybackSpeed() = audio.cyclePlaybackSpeed()

    fun nextStop(tour: Tour, currentStopId: String): Stop? {
        val ordered = tour.stops.sortedBy { it.order }
        val index = ordered.indexOfFirst { it.id == currentStopId }
        if (index < 0 || index >= ordered.lastIndex) return null
        return ordered[index + 1]
    }

    fun previousStop(tour: Tour, currentStopId: String): Stop? {
        val ordered = tour.stops.sortedBy { it.order }
        val index = ordered.indexOfFirst { it.id == currentStopId }
        if (index <= 0) return null
        return ordered[index - 1]
    }

    fun goToNext(tour: Tour) {
        val current = playback.value.stopId ?: return
        nextStop(tour, current)?.let { selectStop(tour, it) }
    }

    fun goToPrevious(tour: Tour) {
        val current = playback.value.stopId ?: return
        previousStop(tour, current)?.let { selectStop(tour, it) }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                audio.tickPosition()
                delay(400)
            }
        }
    }

    override fun onCleared() {
        tickerJob?.cancel()
        locationMonitor.stop()
        audio.release()
        super.onCleared()
    }
}
