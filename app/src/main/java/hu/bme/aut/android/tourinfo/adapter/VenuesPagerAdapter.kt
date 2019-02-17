package hu.bme.aut.android.tourinfo.adapter

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import hu.bme.aut.android.tourinfo.VenueTypeListFragment

class VenuesPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(i: Int): Fragment {
        val fragment = VenueTypeListFragment()
        fragment.arguments = Bundle().apply {
            putInt("TYPE", i)
        }
        return fragment
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0) "Venues" else "Restaurants"
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        //do nothing
    }
}
