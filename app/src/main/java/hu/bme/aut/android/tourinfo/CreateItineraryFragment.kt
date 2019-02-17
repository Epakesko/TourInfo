package hu.bme.aut.android.tourinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import hu.bme.aut.android.tourinfo.model.*
import hu.bme.aut.android.tourinfo.network.GooglePlacesInteractor
import kotlinx.android.synthetic.main.create_itinerary.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.HashMap

class CreateItineraryFragment : Fragment() {
    private lateinit var venueMethodsListener: VenueMethodsListener
    private var mode: String = ""
    private val placesInteractor = GooglePlacesInteractor()
    private var days: Int = 5
    private lateinit var distanceMatrix: TopDistanceMatrixResponse
    private var selectedRestaurants: MutableList<Venue> = mutableListOf()
    private var selectedVenues: MutableList<Venue> = mutableListOf()
    private var notVisitedRestaurantsIndices: MutableList<Int> = mutableListOf()
    private var notVisitedVenuesIndices: MutableList<Int> = mutableListOf()
    private lateinit var itineraryDays: MutableList<Day>
    private lateinit var numberOfPlacesToVisitEachDay: MutableList<HashMap<String, Int>>
    private var indexOfLastVenue: Int = -1

    companion object {
        const val ON_FOOT = "walking"
        const val BY_CAR = "driving"
        const val PUBLIC_TRANSPORT = "public_transport"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        venueMethodsListener = activity as VenueMethodsListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_itinerary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedRestaurants = venueMethodsListener.getRestaurants()
        selectedVenues = venueMethodsListener.getVenues()

        btnCreate.setOnClickListener {
            val latLngs = mutableListOf<String>()
            selectedRestaurants.forEach{
                latLngs.add(it.location.lat.toString()+","+it.location.lng.toString())
            }
            selectedVenues.forEach{
                latLngs.add(it.location.lat.toString()+","+it.location.lng.toString())
            }
            progressBar.visibility = View.VISIBLE
            placesInteractor.getDistanceMatrix(latLngs, mode)
        }

        rbNumberOfDays.setOnRangeBarChangeListener{ _, _, _, _, rightPinValue ->
            days = rightPinValue.toInt()
        }

        rgTravelType.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            when (selectedRadioButton.text){
                resources.getString(R.string.on_foot) -> mode = ON_FOOT
                resources.getString(R.string.public_transport) -> mode = PUBLIC_TRANSPORT
                resources.getString(R.string.by_car) -> mode = BY_CAR
            }
        }
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSuccessEvent(event: DistanceMatrixEvent) {
        notVisitedRestaurantsIndices = mutableListOf()
        notVisitedVenuesIndices = mutableListOf()
        repeat(selectedRestaurants.size) {
            notVisitedRestaurantsIndices.add(it)
        }
        repeat(selectedVenues.size) {
            notVisitedVenuesIndices.add(it + selectedRestaurants.size)
        }

        progressBar.visibility = View.GONE

        distanceMatrix = event.response

        createItinerary()

        venueMethodsListener.setDays(itineraryDays)

        val viewItineraryFragment = ViewItineraryFragment()
        val ft = fragmentManager!!.beginTransaction()
        ft.replace(R.id.fragmentContainer, viewItineraryFragment, "ITINERARY")
        ft.addToBackStack("ITINERARY")
        ft.commit()
    }

    private fun createItinerary(){
        itineraryDays = mutableListOf()
        numberOfPlacesToVisitEachDay = mutableListOf()
        var restaurantsPerDay = selectedRestaurants.size / days
        var extraRestaurants = selectedRestaurants.size.rem(days)
        if(restaurantsPerDay >= 3){
            restaurantsPerDay = 3
            extraRestaurants = 0
        }
        var venuesPerDay = selectedVenues.size / days
        var extraVenues = selectedVenues.size.rem(days)

        repeat(days){
            val numberMap = HashMap<String, Int>()
            if (extraRestaurants > 0){
                extraRestaurants--
                numberMap["restaurants"] = restaurantsPerDay + 1
            }
            else{
                numberMap["restaurants"] = restaurantsPerDay
            }
            if (extraVenues > 0){
                extraVenues--
                numberMap["venues"] = venuesPerDay + 1
            }
            else{
                numberMap["venues"] = venuesPerDay
            }
            numberOfPlacesToVisitEachDay.add(numberMap)
        }

        numberOfPlacesToVisitEachDay.forEachIndexed { index, hashMap ->
            when(hashMap["restaurants"]){
                0 -> itineraryDays.add(index, dayWithoutRestaurant(hashMap["venues"] as Int))
                1 -> itineraryDays.add(index, dayWith1Restaurant(hashMap["venues"] as Int))
                2 -> itineraryDays.add(index, dayWith2Restaurants(hashMap["venues"] as Int))
                3 -> itineraryDays.add(index, dayWith3Restaurants(hashMap["venues"] as Int))
            }
        }
    }

    private fun dayWithoutRestaurant(numberOfVenuesToVisit: Int): Day{
        val dailyVenues = mutableListOf<Venue>()
        val dailyDistances = mutableListOf<MatrixElement>()

        if(numberOfVenuesToVisit == 0) return Day(dailyVenues, dailyDistances)

        val random = Random()
        val randomIndex = random.nextInt(notVisitedVenuesIndices.size)
        indexOfLastVenue = notVisitedVenuesIndices[randomIndex]
        notVisitedVenuesIndices.removeAt(randomIndex)
        dailyVenues.add(selectedVenues[indexOfLastVenue - selectedRestaurants.size])


        repeat(numberOfVenuesToVisit-1){
            val previousIndex = indexOfLastVenue
            dailyVenues.add(getClosestVenueToVenue(indexOfLastVenue))
            dailyDistances.add(distanceMatrix.rows[previousIndex].elements[indexOfLastVenue])
        }

        return Day(dailyVenues, dailyDistances)
    }

    private fun dayWith1Restaurant(numberOfVenuesToVisit: Int): Day{
        val venuesBeforeRestaurant = Math.ceil(numberOfVenuesToVisit / 3.0).toInt()
        val venuesAfterRestaurant = numberOfVenuesToVisit - venuesBeforeRestaurant

        val previousEvents = dayWithoutRestaurant(venuesBeforeRestaurant)
        val dailyVenues = previousEvents.venues
        val dailyDistances = previousEvents.distances

        var previousIndex = indexOfLastVenue
        dailyVenues.add(getClosestRestaurantToVenue(indexOfLastVenue))
        dailyDistances.add(distanceMatrix.rows[previousIndex].elements[indexOfLastVenue])

        repeat(venuesAfterRestaurant){
            previousIndex = indexOfLastVenue
            dailyVenues.add(getClosestVenueToVenue(indexOfLastVenue))
            dailyDistances.add(distanceMatrix.rows[previousIndex].elements[indexOfLastVenue])
        }

        return Day(dailyVenues, dailyDistances)
    }

    private fun dayWith2Restaurants(numberOfVenuesToVisit: Int): Day{
        val previousEvents = dayWith1Restaurant(numberOfVenuesToVisit)
        val dailyVenues = previousEvents.venues
        val dailyDistances = previousEvents.distances

        val previousIndex = indexOfLastVenue
        dailyVenues.add(getClosestRestaurantToVenue(indexOfLastVenue))
        dailyDistances.add(distanceMatrix.rows[previousIndex].elements[indexOfLastVenue])

        return Day(dailyVenues, dailyDistances)
    }

    private fun dayWith3Restaurants(numberOfVenuesToVisit: Int): Day{
        val venuesBeforeRestaurant = Math.ceil(numberOfVenuesToVisit / 3.0) as Int
        val venuesAfterRestaurant = numberOfVenuesToVisit - venuesBeforeRestaurant
        val dailyVenues = mutableListOf<Venue>()
        val dailyDistances = mutableListOf<MatrixElement>()
        val random = Random()

        val randomIndex = random.nextInt(notVisitedRestaurantsIndices.size)
        indexOfLastVenue = notVisitedRestaurantsIndices[randomIndex]
        notVisitedRestaurantsIndices.removeAt(randomIndex)
        dailyVenues.add(selectedVenues[indexOfLastVenue])

        repeat(venuesBeforeRestaurant){
            val previousIndex = indexOfLastVenue
            dailyVenues.add(getClosestVenueToVenue(indexOfLastVenue))
            dailyDistances.add(distanceMatrix.rows[previousIndex].elements[indexOfLastVenue])
        }

        var previousIndex = indexOfLastVenue
        dailyVenues.add(getClosestRestaurantToVenue(indexOfLastVenue))
        dailyDistances.add(distanceMatrix.rows[previousIndex].elements[indexOfLastVenue])

        repeat(venuesAfterRestaurant){
            previousIndex = indexOfLastVenue
            dailyVenues.add(getClosestVenueToVenue(indexOfLastVenue))
            dailyDistances.add(distanceMatrix.rows[previousIndex].elements[indexOfLastVenue])
        }



        return Day(dailyVenues, dailyDistances)
    }

    private fun getClosestVenueToVenue(venueIndex: Int): Venue{
        indexOfLastVenue = getMinVenueDistanceIndex(venueIndex)
        val minDistanceIndex = notVisitedVenuesIndices.lastIndexOf(indexOfLastVenue)
        notVisitedVenuesIndices.removeAt(minDistanceIndex)
        return selectedVenues[indexOfLastVenue - selectedRestaurants.size]
    }

    private fun getClosestRestaurantToVenue(venueIndex: Int): Venue{
        indexOfLastVenue = getMinRestaurantDistanceIndex(venueIndex)
        val minDistanceIndex = notVisitedRestaurantsIndices.lastIndexOf(indexOfLastVenue)
        notVisitedRestaurantsIndices.removeAt(minDistanceIndex)
        return selectedRestaurants[indexOfLastVenue]
    }


    private fun getMinVenueDistanceIndex(index: Int): Int {
        var min = Int.MAX_VALUE
        var minIndex = -1
        distanceMatrix.rows[index].elements.forEachIndexed { index2, elem ->
            if(index2 < selectedRestaurants.size) return@forEachIndexed
            if(elem.duration.value < min && notVisitedVenuesIndices.contains(index2)){
                min = elem.duration.value
                minIndex = index2
            }
        }
        return minIndex
    }

    private fun getMinRestaurantDistanceIndex(index: Int): Int {
        var min = Int.MAX_VALUE
        var minIndex = -1
        distanceMatrix.rows[index].elements.forEachIndexed { index2, elem ->
            if(index2 >= selectedRestaurants.size) return@forEachIndexed
            if(elem.duration.value < min && notVisitedRestaurantsIndices.contains(index2)){
                min = elem.duration.value
                minIndex = index2
            }
        }
        return minIndex
    }

}