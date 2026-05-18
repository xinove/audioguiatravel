package com.audioguiatravel.edinburgh.location

import com.audioguiatravel.edinburgh.data.Stop
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object GeoUtils {

    fun distanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Float {
        val earthRadius = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }

    fun findNearestStop(
        userLat: Double,
        userLon: Double,
        stops: List<Stop>,
    ): Pair<Stop, Float>? {
        return stops
            .map { stop ->
                stop to distanceMeters(userLat, userLon, stop.latitude, stop.longitude)
            }
            .minByOrNull { it.second }
            ?.let { it.first to it.second }
    }

    fun findArrivedStop(
        userLat: Double,
        userLon: Double,
        stops: List<Stop>,
    ): Stop? {
        return stops.firstOrNull { stop ->
            distanceMeters(userLat, userLon, stop.latitude, stop.longitude) <= stop.geofenceRadiusMeters
        }
    }
}
