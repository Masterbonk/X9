package dk.itu.moapd.x9.ADJU.model

import android.content.Context
import dk.itu.moapd.x9.ADJU.R

data class CurrentLocation(
    val latitude: String,
    val longitude: String,
    val altitude: String,
    val speed: String,
    val time: String,
) {
    /**
     * A set of private constants used in this class.
     */
    companion object {

        /**
         * Creates a `Location` with not available values.
         *
         * @param context The context to use for getting the string resources.
         *
         * @return A `Location` with not available values.
         */
        fun notAvailable(context: Context): CurrentLocation {
            val na = context.getString(R.string.text_not_available)
            return CurrentLocation(
                latitude = na,
                longitude = na,
                altitude = na,
                speed = na,
                time = na,
            )
        }
    }
}