package dk.itu.moapd.x9.ADJU.mapper

import android.content.Context
import android.location.Location
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.model.CurrentLocation
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Maps a `Location` to a `CurrentLocation`.
 *
 * @param context The context to use for getting the string resources.
 * @param location The `Location` to map.
 *
 * @return A `CurrentLocation` mapped from the `Location`.
 */
fun fieldsFromLocation(context: Context, location: Location): CurrentLocation {
    return CurrentLocation(
        latitude = String.format(Locale.getDefault(), "%.6f", location.latitude),
        longitude = String.format(Locale.getDefault(), "%.6f", location.longitude),
        altitude = String.format(Locale.getDefault(), "%.6f", location.altitude),
        speed = context.getString(R.string.text_speed_km, (location.speed * 3.6f).toInt()),
        time = location.time.toSimpleDateTimeString(),
    )
}

fun Long.toSimpleDateTimeString(locale: Locale = Locale.getDefault()): String {
    val dateFormat = SimpleDateFormat("E, MMM dd yyyy hh:mm:ss a", locale)
    return dateFormat.format(this)
}