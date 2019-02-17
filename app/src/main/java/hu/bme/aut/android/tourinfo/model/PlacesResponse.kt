package hu.bme.aut.android.tourinfo.model

data class PlacesResponse(
    val groups: List<Group>,
    val totalResults: Int
)