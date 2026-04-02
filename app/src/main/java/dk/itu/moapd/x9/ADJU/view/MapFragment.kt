package dk.itu.moapd.x9.ADJU.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.core.preferences.LocationTrackingPreferences
import dk.itu.moapd.x9.ADJU.databinding.FragmentMainBinding
import dk.itu.moapd.x9.ADJU.service.LocationService
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.jar.Manifest
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
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.composeView.apply {
            //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                map()
            }
        }
        return binding.root
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

    @Composable
    private fun map(){
        val context = LocalContext.current
        //val snackbarHostState = remember { SnackbarHostState() }

        val scope = rememberCoroutineScope()
        val permissionDeniedMsg = stringResource(R.string.permission_denied_message)
        val itu = remember { LatLng(55.6596, 12.5910) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(itu, 14f)
        }

        var hasPermission by remember {
            mutableStateOf(
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
            if (!granted) {
                /*scope.launch {
                    snackbarHostState.showSnackbar(permissionDeniedMsg)
                }*/
            }
        }

        // Request permission as soon as the screen is shown (equivalent to the original map-ready flow).
        LaunchedEffect(Unit) {
            if (!hasPermission) {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        val mapStyle = remember {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.maps_style)
        }

        Scaffold(
            //snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                    mapStyleOptions = mapStyle,
                    isMyLocationEnabled = hasPermission,
                ),
            ) {

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
