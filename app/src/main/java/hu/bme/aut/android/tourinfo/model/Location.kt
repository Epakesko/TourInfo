package hu.bme.aut.android.tourinfo.model

data class Location (
    val lat: Double,
    val lng: Double,
    val formattedAddress: List<String>
)