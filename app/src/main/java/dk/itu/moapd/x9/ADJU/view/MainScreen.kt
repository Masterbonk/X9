package dk.itu.moapd.x9.ADJU.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.model.CurrentLocation

@Composable
fun MainScreen(
    paddingValues: PaddingValues,
    location: CurrentLocation,
    trackingEnabled: Boolean,
    onToggleTracking: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = dimensionResource(id = R.dimen.margin_medium))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_medium)))

            ReadOnlyFilledField(label = stringResource(R.string.text_latitude), value = location.latitude)
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_medium)))

            ReadOnlyFilledField(label = stringResource(R.string.text_longitude), value = location.longitude)
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_medium)))

            ReadOnlyFilledField(label = stringResource(R.string.text_altitude), value = location.altitude)
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_medium)))

            ReadOnlyFilledField(label = stringResource(R.string.text_speed), value = location.speed)
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_medium)))

            ReadOnlyFilledField(label = stringResource(R.string.text_time), value = location.time)
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_medium)))

            Button(onClick = onToggleTracking) {
                Text(text = stringResource(id = if (trackingEnabled) R.string.button_stop else R.string.button_start))
            }
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_medium)))
        }
    }
}

@Composable
fun ReadOnlyFilledField(label: String, value: String ) {
    Row() {
        Text(label)
        Text(value)
    }
}