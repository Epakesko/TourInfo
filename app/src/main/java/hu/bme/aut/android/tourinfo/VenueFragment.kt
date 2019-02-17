package hu.bme.aut.android.tourinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.bme.aut.android.tourinfo.adapter.VenueDetailsPagerAdapter
import hu.bme.aut.android.tourinfo.adapter.VenuesPagerAdapter
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import hu.bme.aut.android.tourinfo.model.Venue
import hu.bme.aut.android.tourinfo.network.FourSquarePlacesInteractor
import kotlinx.android.synthetic.main.view_pager.*

class VenueFragment : Fragment(){
    private lateinit var venueMethodsListener: VenueMethodsListener
    private lateinit var mDemoCollectionPagerAdapter: VenueDetailsPagerAdapter
    private lateinit var mViewPager: ViewPager
    private lateinit var myContext: FragmentActivity

    companion object {
        const val VENUE_ID = "venueId"
        const val IS_REST = "isRestaurant"

        fun newInstance(venueId: String, isRestaurant: Boolean = false) = VenueFragment().apply {
            arguments = Bundle().apply {
                putString(VENUE_ID, venueId)
                putBoolean(IS_REST, isRestaurant)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myContext = activity as FragmentActivity
        venueMethodsListener = activity as VenueMethodsListener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewPager = view_pager
        tabs.setupWithViewPager(mViewPager)
        mDemoCollectionPagerAdapter = VenueDetailsPagerAdapter(myContext.supportFragmentManager, arguments!!.getString(VENUE_ID) as String, arguments!!.getBoolean(IS_REST) as Boolean)
        mViewPager.adapter = mDemoCollectionPagerAdapter
    }
}