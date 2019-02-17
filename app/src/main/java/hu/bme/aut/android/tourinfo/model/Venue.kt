package hu.bme.aut.android.tourinfo.model

data class Venue(
    val name: String,
    val location: Location,
    val id: String,
    val description: String,
    val categories: List<Category>,
    val url: String,
    val rating: Double,
    val bestPhoto: BestPhoto,
    val price: Price,
    val hours: Hours
)