package com.audioguiatravel.edinburgh.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.audioguiatravel.edinburgh.data.Stop
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationMonitor(context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    private val _proximity = MutableStateFlow(ProximityState())
    val proximity: StateFlow<ProximityState> = _proximity.asStateFlow()

    private var activeStops: List<Stop> = emptyList()
    private var lastAnnouncedStopId: String? = null

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            val user = UserLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracyMeters = location.accuracy,
            )
            val nearest = GeoUtils.findNearestStop(user.latitude, user.longitude, activeStops)
            val arrived = GeoUtils.findArrivedStop(user.latitude, user.longitude, activeStops)
            val arrivedForUi = arrived?.takeIf { it.id != lastAnnouncedStopId }

            _proximity.value = _proximity.value.copy(
                userLocation = user,
                nearestStop = nearest?.first,
                distanceToNearestMeters = nearest?.second,
                arrivedStop = arrivedForUi,
            )
        }
    }

    fun setPermissionGranted(granted: Boolean) {
        _proximity.value = _proximity.value.copy(hasLocationPermission = granted)
    }

    fun setActiveStops(stops: List<Stop>) {
        activeStops = stops
        lastAnnouncedStopId = null
    }

    fun acknowledgeArrival(stopId: String) {
        lastAnnouncedStopId = stopId
        _proximity.value = _proximity.value.copy(arrivedStop = null)
    }

    @SuppressLint("MissingPermission")
    fun start() {
        if (!_proximity.value.hasLocationPermission) return
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .setMinUpdateDistanceMeters(5f)
            .build()
        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    fun stop() {
        fusedClient.removeLocationUpdates(callback)
        _proximity.value = ProximityState(hasLocationPermission = _proximity.value.hasLocationPermission)
    }
}
