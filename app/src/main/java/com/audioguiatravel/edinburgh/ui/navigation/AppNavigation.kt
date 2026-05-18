package com.audioguiatravel.edinburgh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.audioguiatravel.edinburgh.ui.TourViewModel
import com.audioguiatravel.edinburgh.ui.screens.HomeScreen
import com.audioguiatravel.edinburgh.ui.screens.PlayerScreen
import com.audioguiatravel.edinburgh.ui.screens.TourDetailScreen

object Routes {
    const val HOME = "home"
    const val TOUR = "tour/{tourId}"
    const val PLAYER = "tour/{tourId}/stop/{stopId}"

    fun tour(tourId: String) = "tour/$tourId"
    fun player(tourId: String, stopId: String) = "tour/$tourId/stop/$stopId"
}

@Composable
fun AppNavigation(viewModel: TourViewModel) {
    val navController = rememberNavController()
    val downloadStates by viewModel.downloadStates.collectAsState()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                cityName = viewModel.cityName,
                tours = viewModel.tours,
                downloadStates = downloadStates,
                downloadStateFor = viewModel::downloadStateFor,
                onDownloadTour = viewModel::requestTourDownload,
                onTourClick = { tourId -> navController.navigate(Routes.tour(tourId)) },
            )
        }

        composable(
            route = Routes.TOUR,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType }),
        ) { entry ->
            val tourId = entry.arguments?.getString("tourId") ?: return@composable
            val tour = remember(tourId) { viewModel.getTour(tourId) } ?: return@composable

            TourDetailScreen(
                tour = tour,
                downloadState = viewModel.downloadStateFor(tour),
                onDownload = { viewModel.requestTourDownload(tour) },
                onBack = { navController.popBackStack() },
                onStopClick = { stop ->
                    viewModel.selectStop(tour, stop)
                    navController.navigate(Routes.player(tourId, stop.id))
                },
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(
                navArgument("tourId") { type = NavType.StringType },
                navArgument("stopId") { type = NavType.StringType },
            ),
        ) { entry ->
            val tourId = entry.arguments?.getString("tourId") ?: return@composable
            val stopId = entry.arguments?.getString("stopId") ?: return@composable
            val tour = remember(tourId) { viewModel.getTour(tourId) } ?: return@composable
            val stop = remember(stopId) { tour.stops.find { it.id == stopId } } ?: return@composable
            val playbackState by viewModel.playback.collectAsState()
            val activeStopId = playbackState.stopId ?: stopId
            val script = remember(tourId, activeStopId) {
                viewModel.getScript(tourId, activeStopId)
            }

            PlayerScreen(
                tour = tour,
                stop = stop,
                playback = viewModel.playback,
                proximity = viewModel.proximity,
                script = script,
                onBeginTracking = { viewModel.beginTourTracking(tour) },
                onEndTracking = viewModel::endTourTracking,
                onBack = { navController.popBackStack() },
                onTogglePlayPause = viewModel::togglePlayPause,
                onSeek = viewModel::seekTo,
                onSeekBy = viewModel::seekBy,
                onNext = { viewModel.goToNext(tour) },
                onPrevious = { viewModel.goToPrevious(tour) },
                onSelectStop = { selected ->
                    viewModel.selectStop(tour, selected)
                    navController.navigate(Routes.player(tourId, selected.id)) {
                        popUpTo(Routes.player(tourId, stopId)) { inclusive = true }
                    }
                },
                onArrivalPlay = { arrived ->
                    viewModel.playArrivedStop(tour, arrived)
                },
                onDismissArrival = viewModel::acknowledgeArrival,
                onCyclePlaybackSpeed = viewModel::cyclePlaybackSpeed,
                imageUriForAsset = { path -> viewModel.assetUri(tour, path) },
            )
        }
    }
}
