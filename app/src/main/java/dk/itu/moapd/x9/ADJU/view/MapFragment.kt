package dk.itu.moapd.x9.ADJU.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.core.preferences.LocationTrackingPreferences
import dk.itu.moapd.x9.ADJU.databinding.FragmentMainBinding
import dk.itu.moapd.x9.ADJU.service.LocationService
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.getValue

class MapFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }

    /**
     * Provides location updates for while-in-use feature.
     */
    private var locationService: LocationService? = null

    /**
     * A flag to indicate whether a bound to the service.
     */
    private var locationServiceBound: Boolean = false

    /**
     * A job for collecting location updates.
     */
    private var collectJob: Job? = null


    private var onLocationCallback: ((Location) -> Unit)? = null

    /**
     * When the user toggles tracking, we may need to start the service even if it's not yet bound.
     * This flag indicates a pending request to subscribe to location updates.
     */
    private var pendingStartTracking: Boolean = false

    /**
     * Defines callbacks for service binding, passed to `bindService()`.
     */
    private val serviceConnection = createLocationServiceConnection(
        onConnected = { service ->
            locationService = service
            locationServiceBound = true

            if (pendingStartTracking) {
                service.subscribeToLocationUpdates()
                pendingStartTracking = false
            }

            startCollectingIfReady()
        },
        onDisconnected = {
            locationService = null
            locationServiceBound = false
            collectJob?.cancel()
            collectJob = null
        },
    )


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyLocationScaffold(
                    sharedPreferences = sharedPreferences,
                    onStartTracking = {
                        pendingStartTracking = true
                        startLocationService()
                        if (locationServiceBound) {
                            locationService?.subscribeToLocationUpdates()
                            pendingStartTracking = false
                        }
                    },
                    onStopTracking = {
                        pendingStartTracking = false
                        locationService?.unsubscribeToLocationUpdates()
                        requireContext().stopService(
                            Intent(requireContext(), LocationService::class.java)
                        )
                    },
                    onCollectLocations = { onLocation ->
                        onLocationCallback = onLocation
                        collectJob?.cancel()
                        startCollectingIfReady()
                    },
                )
            }
            val alreadyEnabledOnCreate = LocationTrackingPreferences.isTrackingEnabled(requireContext())
            if (alreadyEnabledOnCreate) {
                pendingStartTracking = true
                startLocationService()
            }
        }
    }

    /**
     * Called after `onCreate()` or after `onRestart()` when the activity had been stopped, but is
     * now again being displayed to the user. It will usually be followed by `onResume()`. This is a
     * good place to begin drawing visual elements, running animations, etc.
     *
     * You can call `finish()` from within this function, in which case `onStop()` will be
     * immediately called after `onStart()` without the lifecycle transitions in-between
     * (`onResume()`, `onPause()`, etc) executing.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onStart() {
        super.onStart()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        requireContext().bindService(
            Intent(
                requireContext(),
                LocationService::class.java
            ),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        val alreadyEnabled = LocationTrackingPreferences.isTrackingEnabled(requireContext())
        if (alreadyEnabled) {
            pendingStartTracking = true
            startLocationService()
            if (locationServiceBound) {
                locationService?.subscribeToLocationUpdates()
                pendingStartTracking = false
            }
        }
    }

    /**
     * Called when you are no longer visible to the user. You will next receive either
     * `onRestart()`, `onDestroy()`, or nothing, depending on later user activity. This is a good
     * place to stop refreshing UI, running animations and other visual things.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onStop() {
        if (locationServiceBound) {
            requireContext().unbindService(serviceConnection)
            locationServiceBound = false
        }

        collectJob?.cancel()
        collectJob = null

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    /**
     * Called when a shared preference is changed, added, or removed. This may be called even if a
     * preference is set to its existing value. This callback will be run on your main thread.
     *
     * @param sharedPreferences The `SharedPreferences` that received the change.
     * @param key The key of the preference that was changed, added, or removed. Apps targeting
     *      android.os.Build.VERSION_CODES#R on devices running OS versions
     *      android.os.Build.VERSION_CODES#R Android R} or later, will receive a `null` value when
     *      preferences are cleared.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == LocationTrackingPreferences.KEY_TRACKING_ENABLED) {
            val enabled = LocationTrackingPreferences.isTrackingEnabled(requireContext())
            if (!enabled) {
                collectJob?.cancel()
                collectJob = null
            } else {
                startCollectingIfReady()
            }
        }
    }

    /**
     * Starts the LocationService as a foreground service.
     * Uses startForegroundService() on Android O+ and startService() on older versions.
     */
    private fun startLocationService() {
        val serviceIntent = Intent(requireContext(), LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(requireContext(), serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }

    /**
     * Starts the collector if we have both the service bound and the composable's onLocation
     * callback available and tracking is enabled.
     */
    private fun startCollectingIfReady() {
        val isReady = onLocationCallback != null &&
                locationService != null &&
                LocationTrackingPreferences.isTrackingEnabled(requireContext())

        if (!isReady) return

        collectJob?.cancel()
        collectJob = lifecycleScope.launch {
            locationService?.locationUpdates?.collect { location ->
                onLocationCallback?.invoke(location)
            }
        }
    }
}
