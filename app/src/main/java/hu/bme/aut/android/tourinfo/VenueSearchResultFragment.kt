package hu.bme.aut.android.tourinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import hu.bme.aut.android.tourinfo.adapter.VenuesAdapter
import android.support.v7.widget.LinearLayoutManager
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import hu.bme.aut.android.tourinfo.model.SearchVenuesEvent
import hu.bme.aut.android.tourinfo.model.Venue
import hu.bme.aut.android.tourinfo.network.PlacesAPI
import hu.bme.aut.android.tourinfo.network.FourSquarePlacesInteractor
import kotlinx.android.synthetic.main.list_venues.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class VenueSearchResultFragment : Fragment(), VenuesAdapter.VenueItemClickListener {
    override fun onItemClick(venueId: String) {
        val fragment = VenueFragment.newInstance(venueId, arguments?.getBoolean(IS_REST) as Boolean)
        val ft = fragmentManager!!.beginTransaction()
        ft.replace(R.id.fragmentContainer, fragment, "VENUE_DETAILS")
        ft.addToBackStack("VENUE_DETAILS")
        ft.commit()
    }

    var offset: Int = 0
    private val placesInteractor = FourSquarePlacesInteractor()

    companion object {
        private const val LAT_LNG = "latLng"
        private const val SCOPE = "scope"
        private const val PRICE = "price"
        private const val IS_REST = "isRestaurant"

        fun newInstance(ll: String, scope: String, price: String, isRestaurant: Boolean) = VenueSearchResultFragment().apply {
            arguments = Bundle().apply {
                putString(LAT_LNG, ll)
                putString(SCOPE, scope)
                putString(PRICE, price)
                putBoolean(IS_REST, isRestaurant)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_venues, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvVenues.layoutManager = LinearLayoutManager(activity)

        btnPreviousResults.visibility = View.GONE
        btnNextResults.visibility = View.VISIBLE
        btnCreateItinerary.visibility = View.GONE

        btnPreviousResults.setOnClickListener {
            offset -= 25
            placesInteractor.searchVenues(
                    latLng = arguments?.getString(LAT_LNG) as String,
                    section = arguments?.getString(SCOPE) as String,
                    offset = offset,
                    price = arguments?.getString(PRICE) as String
            )
        }

        btnNextResults.setOnClickListener {
            offset += 25
            placesInteractor.searchVenues(
                    latLng = arguments?.getString(LAT_LNG) as String,
                    section = arguments?.getString(SCOPE) as String,
                    offset = offset,
                    price = arguments?.getString(PRICE) as String
            )
        }

        placesInteractor.searchVenues(
                latLng = arguments?.getString(LAT_LNG) as String,
                section = arguments?.getString(SCOPE) as String,
                offset = offset,
                price = arguments?.getString(PRICE) as String
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSuccessEvent(event: SearchVenuesEvent) {
        val listOfVenues: MutableList<Venue> = mutableListOf()
        event.response.response.groups.forEach { group ->
            group.items.forEach { item ->
                listOfVenues += item.venue
            }
        }
        progressBar.visibility = View.GONE
        rvVenues.adapter = VenuesAdapter(listOfVenues)
        (rvVenues.adapter as VenuesAdapter).itemClickListener = this
        btnPreviousResults.visibility = if(offset == 0) View.GONE else View.VISIBLE
        btnNextResults.visibility = if(offset + PlacesAPI.FOURSQUARE_LIMIT < event.response.response.totalResults) View.VISIBLE else View.GONE
    }
}