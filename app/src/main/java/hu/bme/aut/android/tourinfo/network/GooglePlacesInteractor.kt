package hu.bme.aut.android.tourinfo.network

import hu.bme.aut.android.tourinfo.model.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GooglePlacesInteractor {
    private val placesApi: PlacesAPI

    companion object {
        enum class RequestType { DISTANCE_MATRIX }
    }

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(PlacesAPI.GOOGLE_ENDPOINT_URL)
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
                    RequestType.DISTANCE_MATRIX -> EventBus.getDefault().post(DistanceMatrixEvent(response = response as TopDistanceMatrixResponse))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun getDistanceMatrix(
            latLngs: List<String>,
            mode: String
    ) {
        val places = latLngs.joinToString(separator = "|")
        val distanceMatrix = placesApi.getDistanceMatrix(PlacesAPI.GOOGLE_API_KEY, places, places, mode)
        runCallOnBackgroundThread(distanceMatrix, RequestType.DISTANCE_MATRIX)
    }
}