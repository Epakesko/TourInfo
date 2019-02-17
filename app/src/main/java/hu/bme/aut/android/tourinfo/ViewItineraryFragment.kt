package hu.bme.aut.android.tourinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.bme.aut.android.tourinfo.adapter.DaysPagerAdapter
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import android.support.v4.app.FragmentActivity
import kotlinx.android.synthetic.main.view_pager.*


class ViewItineraryFragment : Fragment() {
    private lateinit var venueMethodsListener: VenueMethodsListener
    private lateinit var mDemoCollectionPagerAdapter: DaysPagerAdapter
    private lateinit var mViewPager: ViewPager
    private lateinit var myContext: FragmentActivity

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
        mDemoCollectionPagerAdapter = DaysPagerAdapter(myContext.supportFragmentManager, venueMethodsListener.getDays().size)
        mViewPager.adapter = mDemoCollectionPagerAdapter
    }
}