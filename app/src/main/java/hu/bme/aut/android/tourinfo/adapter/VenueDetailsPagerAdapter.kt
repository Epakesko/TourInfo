package hu.bme.aut.android.tourinfo.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.os.Parcelable
import hu.bme.aut.android.tourinfo.VenueDetailsFragment
import hu.bme.aut.android.tourinfo.VenueMapFragment


class VenueDetailsPagerAdapter(fm: FragmentManager, val venueId: String, val isRestaurant: Boolean) : FragmentStatePagerAdapter(fm) {

    override fun getItem(i: Int): Fragment {
        return if(i == 0) VenueDetailsFragment.newInstance(venueId, isRestaurant) else VenueMapFragment.newInstance(venueId)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0) "Details" else "Map"
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        //do nothing
    }
}
