package hu.bme.aut.android.tourinfo.listeners

import hu.bme.aut.android.tourinfo.model.Day
import hu.bme.aut.android.tourinfo.model.Venue

interface VenueMethodsListener {
    fun getVenue(id: String): Venue?
    fun addVenue(venue: Venue)
    fun addRestaurant(venue: Venue)
    fun setDays(days: MutableList<Day>)
    fun getVenues(): MutableList<Venue>
    fun getRestaurants(): MutableList<Venue>
    fun getDays(): MutableList<Day>
}
