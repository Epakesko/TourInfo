package hu.bme.aut.android.tourinfo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.venues_menu.*

class VenuesMenuFragment : Fragment() {

    companion object {
        private const val LAT_LNG = "latLng"

        fun newInstance(ll: String) = VenuesMenuFragment().apply {
            arguments = Bundle().apply {
                putString(LAT_LNG, ll)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.venues_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSearchVenues.setOnClickListener {
            val venueSearchFragment = VenueSearchFragment.newInstance(arguments?.getString(LAT_LNG) as String)
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(R.id.fragmentContainer, venueSearchFragment, "VENUES_SEARCH")
            ft.addToBackStack("VENUES_SEARCH")
            ft.commit()
        }

        btnViewVenues.setOnClickListener {
            val venueListFragment = VenueListFragment()
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(R.id.fragmentContainer, venueListFragment, "VENUE_LIST")
            ft.addToBackStack("VENUE_LIST")
            ft.commit()
        }

        btnViewItinerary.setOnClickListener {
            val viewItineraryFragment = ViewItineraryFragment()
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(R.id.fragmentContainer, viewItineraryFragment, "ITINERARY")
            ft.addToBackStack("ITINERARY")
            ft.commit()
        }
    }
}