package hu.bme.aut.android.tourinfo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.android.tourinfo.model.Itinerary
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import hu.bme.aut.android.tourinfo.CitySelectionFragment.Companion.FILEPATH
import hu.bme.aut.android.tourinfo.listeners.FileMethodsListener
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import hu.bme.aut.android.tourinfo.model.Day
import hu.bme.aut.android.tourinfo.model.TopDistanceMatrixResponse
import hu.bme.aut.android.tourinfo.model.Venue
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class VenuesActivity : AppCompatActivity(), VenueMethodsListener {

    private lateinit var latLng: String
    private lateinit var itinerary: Itinerary
    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_itenerary)

        filePath = intent.getStringExtra(FILEPATH)

        val gson = Gson()
        val bufferedReader: BufferedReader = File(filePath).bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        itinerary = gson.fromJson(inputString, Itinerary::class.java)

        latLng = itinerary.latLng

        val venueMenuFragment = VenuesMenuFragment.newInstance(latLng)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentContainer, venueMenuFragment, "VENUE_MENU_FRAGMENT")
        ft.commit()
    }

    private fun updateFile(){
        var gson = Gson()
        val file = File(filePath)
        val a = gson.toJson(itinerary)
        file.writeText(a)
    }

    override fun getVenue(id: String): Venue?{
        return (itinerary.selectedPlaces + itinerary.selectedRestaurants).find{
            it.id == id
        }
    }

    override fun addVenue(venue: Venue){
        itinerary.selectedPlaces.add(venue)
        updateFile()
    }

    override fun getVenues(): MutableList<Venue>{
        return itinerary.selectedPlaces
    }

    override fun addRestaurant(venue: Venue){
        itinerary.selectedRestaurants.add(venue)
        updateFile()
    }

    override fun getRestaurants(): MutableList<Venue>{
        return itinerary.selectedRestaurants
    }

    override fun setDays(days: MutableList<Day>) {
        itinerary.days.removeAll { true }
        itinerary.days.addAll(days)
        updateFile()
    }

    override fun getDays(): MutableList<Day> {
        return itinerary.days
    }
}
