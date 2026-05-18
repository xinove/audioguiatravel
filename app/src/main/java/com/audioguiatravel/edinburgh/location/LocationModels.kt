package com.audioguiatravel.edinburgh.location

import com.audioguiatravel.edinburgh.data.Stop

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
)

data class ProximityState(
    val userLocation: UserLocation? = null,
    val nearestStop: Stop? = null,
    val distanceToNearestMeters: Float? = null,
    val arrivedStop: Stop? = null,
    val hasLocationPermission: Boolean = false,
)
