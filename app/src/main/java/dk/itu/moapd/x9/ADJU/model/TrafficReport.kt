package dk.itu.moapd.x9.ADJU.model

/**
 * A model class with all parameters to represent a dummy object in the Live View application.
 */
data class TrafficReport(
    val title: String = "",
    val description: String = "",
    val state: String = "",
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)