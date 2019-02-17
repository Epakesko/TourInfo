package hu.bme.aut.android.tourinfo.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import hu.bme.aut.android.tourinfo.ItineraryDayFragment
import android.os.Parcelable



class DaysPagerAdapter(fm: FragmentManager, val dayCount: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(i: Int): Fragment {
        val fragment = ItineraryDayFragment()
        fragment.arguments = Bundle().apply {
            putInt("DAY_NUMBER", i)
        }
        return fragment
    }

    override fun getCount(): Int {
        return dayCount
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Day ${position+1}"
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        //do nothing
    }
}
