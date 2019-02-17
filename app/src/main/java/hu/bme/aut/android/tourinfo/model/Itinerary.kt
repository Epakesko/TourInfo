package hu.bme.aut.android.tourinfo.model

import com.google.android.gms.location.places.Place
import java.util.*

data class Itinerary(
        val creationDate: String,
        val selectedRestaurants: MutableList<Venue>,
        val selectedPlaces: MutableList<Venue>,
        val selectedCity: String,
        val latLng: String,
        val days: MutableList<Day>
)