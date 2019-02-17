package hu.bme.aut.android.tourinfo.network

import hu.bme.aut.android.tourinfo.model.TopDetailsResponse
import hu.bme.aut.android.tourinfo.model.TopDistanceMatrixResponse
import hu.bme.aut.android.tourinfo.model.TopSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query



interface PlacesAPI {
    companion object {
        const val FOURSQUARE_ENDPOINT_URL = "https://api.foursquare.com/v2/"
        const val FOURSQUARE_API_KEY = "EUSBG30SNMX4C2Q3SUEA44XAJSEGDVBGSGYQMASJW1WPARMI"
        const val FOURSQUARE_API_SECRET = "KXC5ZT5M0COXMYLTRL1MD0AL5GWOJ4AFITYFWBHYZZO3PZXT"
        const val FOURSQUARE_VERSION = "20181105"
        const val FOURSQUARE_RADIUS = "100000"
        const val FOURSQUARE_LIMIT = 25

        const val GOOGLE_ENDPOINT_URL = "https://maps.googleapis.com/maps/api/"
        const val GOOGLE_API_KEY = "AIzaSyBLbHuW-At8-CC5BYzbGdOn6BgbWi_KJoY"
    }

    @GET("venues/explore")
    fun searchVenues(@Query("client_id") client_id: String,
                     @Query("client_secret") client_secret: String,
                     @Query("v") version: String,
                     @Query("ll") latLng: String,
                     @Query("radius") radius: String,
                     @Query("section") section: String,
                     @Query("limit") limit: Int,
                     @Query("offset") offset: Int
                     ): Call<TopSearchResponse>

    @GET("venues/explore")
    fun searchVenues(@Query("client_id") client_id: String,
                     @Query("client_secret") client_secret: String,
                     @Query("v") version: String,
                     @Query("ll") latLng: String,
                     @Query("radius") radius: String,
                     @Query("section") section: String,
                     @Query("limit") limit: Int,
                     @Query("offset") offset: Int,
                     @Query("price") price: String
    ): Call<TopSearchResponse>

    @GET("venues/{venueId}")
    fun getVenueDetails(@Path("venueId") venueId: String,
                        @Query("client_id") client_id: String,
                        @Query("client_secret") client_secret: String,
                        @Query("v") version: String
                        ): Call<TopDetailsResponse>

    @GET("distancematrix/json")
    fun getDistanceMatrix(@Query("key") key: String,
                          @Query("origins") origins: String,
                          @Query("destinations") destinations: String,
                          @Query("mode") mode: String
                        ): Call<TopDistanceMatrixResponse>

}