package dk.itu.moapd.x9.ADJU.view

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.mapper.fieldsFromLocation
import dk.itu.moapd.x9.ADJU.model.CurrentLocation
import dk.itu.moapd.x9.ADJU.state.rememberTrackingEnabledState
import java.util.jar.Manifest

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_VALUE")
@Composable
fun MyLocationScaffold(
    sharedPreferences: SharedPreferences,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onCollectLocations: (onLocation: (Location) -> Unit) -> Unit,
){
    val context = LocalContext.current
    val trackingEnabled = rememberTrackingEnabledState(sharedPreferences, context)

    var fields by remember { mutableStateOf(CurrentLocation.notAvailable(context)) }

    LaunchedEffect(trackingEnabled.value) {
        if (trackingEnabled.value) {
            onCollectLocations { location ->
                fields = fieldsFromLocation(context, location)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) onStartTracking()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
    ) { padding ->
        MainScreen(
            paddingValues = padding,
            location = fields,
            trackingEnabled = trackingEnabled.value,
            onToggleTracking = {
                if (trackingEnabled.value) {
                    // Stop tracking and immediately reset displayed fields to 'not available'
                    onStopTracking()
                    fields = CurrentLocation.notAvailable(context)
                } else {
                    requestOrStartTracking(
                        context = context,
                        onHasPermission = onStartTracking,
                        onRequestPermission = {
                            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                    )
                }
            },
        )
    }
}

private fun requestOrStartTracking(
    context: Context,
    onHasPermission: () -> Unit,
    onRequestPermission: () -> Unit,
) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    if (hasPermission) onHasPermission() else onRequestPermission()
}