package hu.bme.aut.android.tourinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.venue_map.*
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapView
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import hu.bme.aut.android.tourinfo.model.Venue
import hu.bme.aut.android.tourinfo.model.VenueDetailsEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class VenueMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var venueMethodsListener: VenueMethodsListener
    private lateinit var mMapView: MapView
    private var mMap: GoogleMap? = null
    private var venue: Venue? = null

    companion object {

        fun newInstance(venueId: String, isRestaurant: Boolean = false) = VenueMapFragment().apply {
            arguments = Bundle().apply {
                putString(VenueFragment.VENUE_ID, venueId)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        venueMethodsListener = activity as VenueMethodsListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.venue_map, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val venueId = arguments!!.getString(VenueFragment.VENUE_ID) as String
        mMapView = mapView
        mMapView.onCreate(savedInstanceState)

        mMapView.onResume()

        venue = venueMethodsListener.getVenue(venueId)

        if(venue != null){
            createMap()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        progressBar.visibility = View.GONE

        val venueLocation = LatLng(venue!!.location.lat, venue!!.location.lng)
        mMap!!.addMarker(MarkerOptions().position(venueLocation).title(venue!!.name))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLocation, 15.0f))
    }

    fun createMap(){
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSuccessEvent(event: VenueDetailsEvent) {
        venue = event.response.response.venue

        createMap()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}