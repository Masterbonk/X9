package dk.itu.moapd.x9.ADJU.core

import io.github.cdimascio.dotenv.dotenv

val DATABASE_URL: String = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_URL"]

val MAPS_API_KEY: String = dotenv {
    directory = "/assets"
    filename = "env"
}["MAPS_API_KEY"]

val FIREBASE_STORAGE: String = dotenv {
    directory = "/assets"
    filename = "env"
}["FIREBASE_STORAGE"]

val WEATHER_API_KEY: String = dotenv {
    directory = "/assets"
    filename = "env"
}["WEATHER_API_KEY"]