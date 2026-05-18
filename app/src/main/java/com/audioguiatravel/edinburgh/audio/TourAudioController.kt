package com.audioguiatravel.edinburgh.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.audioguiatravel.edinburgh.data.Stop
import com.audioguiatravel.edinburgh.data.Tour
import com.audioguiatravel.edinburgh.data.TourRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlaybackUiState(
    val tourId: String? = null,
    val stopId: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false,
    val playbackSpeed: Float = 1f,
    val errorMessage: String? = null,
)

class TourAudioController(
    context: Context,
    private val repository: TourRepository,
) {
    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()

    private val _state = MutableStateFlow(PlaybackUiState())
    val state: StateFlow<PlaybackUiState> = _state.asStateFlow()

    private var activeTour: Tour? = null

    private val speedSteps = floatArrayOf(1f, 1.25f, 1.5f, 1.75f, 2f)
    private var speedIndex = 0

    init {
        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    publish()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    publish()
                }

                override fun onPlayerError(error: PlaybackException) {
                    _state.value = _state.value.copy(
                        errorMessage = "No se pudo reproducir el audio. ¿Has añadido el MP3 en assets?",
                        isPlaying = false,
                    )
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int,
                ) {
                    publish()
                }
            },
        )
    }

    fun getPlayer(): ExoPlayer = player

    fun loadStop(tour: Tour, stop: Stop) {
        activeTour = tour
        if (!repository.hasBundledAudio(stop.audioAsset)) {
            _state.value = _state.value.copy(
                tourId = tour.id,
                stopId = stop.id,
                durationMs = stop.durationSeconds * 1000L,
                isPlaying = false,
                errorMessage = repository.missingAudioHint(stop.audioAsset),
            )
            return
        }
        val uri = repository.assetUri(tour, stop.audioAsset)
        val item = MediaItem.Builder()
            .setUri(uri)
            .setMediaId(stop.id)
            .build()
        player.setMediaItem(item)
        player.prepare()
        applyPlaybackSpeed()
        _state.value = _state.value.copy(
            tourId = tour.id,
            stopId = stop.id,
            durationMs = stop.durationSeconds * 1000L,
            errorMessage = null,
        )
        publish()
    }

    fun play() {
        if (_state.value.errorMessage != null) return
        player.play()
        publish()
    }

    fun pause() {
        player.pause()
        publish()
    }

    fun togglePlayPause() {
        if (player.isPlaying) pause() else play()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        publish()
    }

    fun seekBy(deltaMs: Long) {
        seekTo((player.currentPosition + deltaMs).coerceAtLeast(0L))
    }

    fun cyclePlaybackSpeed() {
        speedIndex = (speedIndex + 1) % speedSteps.size
        applyPlaybackSpeed()
        publish()
    }

    private fun applyPlaybackSpeed() {
        val speed = speedSteps[speedIndex]
        player.setPlaybackSpeed(speed)
    }

    fun release() {
        player.release()
    }

    fun tickPosition() {
        publish()
    }

    private fun publish() {
        _state.value = _state.value.copy(
            tourId = activeTour?.id ?: _state.value.tourId,
            stopId = player.currentMediaItem?.mediaId ?: _state.value.stopId,
            isPlaying = player.isPlaying,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            durationMs = player.duration.takeIf { it > 0 } ?: _state.value.durationMs,
            isBuffering = player.playbackState == Player.STATE_BUFFERING,
            playbackSpeed = speedSteps[speedIndex],
            errorMessage = _state.value.errorMessage,
        )
    }
}
