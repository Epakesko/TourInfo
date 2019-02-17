package hu.bme.aut.android.tourinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.bme.aut.android.tourinfo.adapter.VenuesAdapter
import hu.bme.aut.android.tourinfo.adapter.VenuesPagerAdapter
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import kotlinx.android.synthetic.main.list_venues.*

class VenueTypeListFragment : Fragment(), VenuesAdapter.VenueItemClickListener {
    private lateinit var venueMethodsListener: VenueMethodsListener

    override fun onItemClick(venueId: String) {
        val fragment = VenueFragment.newInstance(venueId)
        val ft = fragmentManager!!.beginTransaction()
        ft.replace(R.id.fragmentContainer, fragment, "VENUE_DETAILS")
        ft.addToBackStack("VENUE_DETAILS")
        ft.commit()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        venueMethodsListener = activity as VenueMethodsListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_venues, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var type = -1
        super.onViewCreated(view, savedInstanceState)
        rvVenues.layoutManager = LinearLayoutManager(activity)

        progressBar.visibility = View.GONE
        btnPreviousResults.visibility = View.GONE
        btnNextResults.visibility = View.GONE
        btnCreateItinerary.visibility = View.VISIBLE

        arguments?.takeIf { it.containsKey("TYPE") }?.apply {
            type = getInt("TYPE")
        }

        rvVenues.adapter = if(type == 0) VenuesAdapter(venueMethodsListener.getVenues()) else VenuesAdapter(venueMethodsListener.getRestaurants())
        (rvVenues.adapter as VenuesAdapter).itemClickListener = this

        btnCreateItinerary.setOnClickListener {
            val createItineraryFragment = CreateItineraryFragment()
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(R.id.fragmentContainer, createItineraryFragment, "CREATE_NEW_ITINERARY")
            ft.addToBackStack("CREATE_NEW_ITINERARY")
            ft.commit()
        }
    }
}