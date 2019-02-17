package hu.bme.aut.android.tourinfo.network

import hu.bme.aut.android.tourinfo.VenueSearchFragment.Companion.FOOD
import hu.bme.aut.android.tourinfo.model.SearchVenuesEvent
import hu.bme.aut.android.tourinfo.model.TopDetailsResponse
import hu.bme.aut.android.tourinfo.model.TopSearchResponse
import hu.bme.aut.android.tourinfo.model.VenueDetailsEvent
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FourSquarePlacesInteractor {
    private val placesApi: PlacesAPI

    companion object {
        enum class RequestType { SEARCH, DETAILS, HOURS }
    }

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(PlacesAPI.FOURSQUARE_ENDPOINT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        this.placesApi = retrofit.create(PlacesAPI::class.java)
    }

    private fun <T> runCallOnBackgroundThread(
            call: Call<T>, requestType: RequestType
    ) {
        Thread {
            try {
                val a = call.execute()
                var response = a.body()!!
                when(requestType){
                    RequestType.SEARCH -> EventBus.getDefault().post(SearchVenuesEvent(response = response as TopSearchResponse))
                    RequestType.DETAILS -> EventBus.getDefault().post(VenueDetailsEvent(response = response as TopDetailsResponse))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun searchVenues(
            latLng: String,
            section: String,
            offset: Int,
            price: String
    ) {
        val searchVenuesRequest =  if(section != FOOD) placesApi.searchVenues(PlacesAPI.FOURSQUARE_API_KEY, PlacesAPI.FOURSQUARE_API_SECRET, PlacesAPI.FOURSQUARE_VERSION, latLng, PlacesAPI.FOURSQUARE_RADIUS, section, PlacesAPI.FOURSQUARE_LIMIT, offset)
                                   else placesApi.searchVenues(PlacesAPI.FOURSQUARE_API_KEY, PlacesAPI.FOURSQUARE_API_SECRET, PlacesAPI.FOURSQUARE_VERSION, latLng, PlacesAPI.FOURSQUARE_RADIUS, section, PlacesAPI.FOURSQUARE_LIMIT, offset, price)
        runCallOnBackgroundThread(searchVenuesRequest, RequestType.SEARCH)
    }

    fun getVenueDetails(
            venueId: String
    ){
        val getVenueDetailsRequest = placesApi.getVenueDetails(venueId, PlacesAPI.FOURSQUARE_API_KEY, PlacesAPI.FOURSQUARE_API_SECRET, PlacesAPI.FOURSQUARE_VERSION)
        runCallOnBackgroundThread(getVenueDetailsRequest, RequestType.DETAILS)
    }
}