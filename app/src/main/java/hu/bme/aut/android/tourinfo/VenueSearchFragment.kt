package hu.bme.aut.android.tourinfo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.appyvet.materialrangebar.RangeBar
import kotlinx.android.synthetic.main.search_venues.*

class VenueSearchFragment : Fragment() {

    private var scope: String = "food"
    private var price: String = "1,4"

    companion object {
        private const val LAT_LNG   = "latlng"
        const val FOOD              = "food"
        private const val DRINKS    = "drinks"
        private const val COFFEE    = "coffee"
        private const val ARTS      = "arts"
        private const val SHOPS     = "shops"
        private const val OUTDOORS  = "outdoors"
        private const val SIGHTS    = "sights"
        private const val TRENDING  = "trending"
        private const val TOP_PICKS = "topPicks"

        fun newInstance(ll: String) = VenueSearchFragment().apply {
            arguments = Bundle().apply {
                putString(LAT_LNG, ll)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_venues, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSearchPlaces.setOnClickListener {
            val venueSearchResultFragment = VenueSearchResultFragment.newInstance(arguments?.getString(LAT_LNG) as String, scope, price, scope == FOOD)
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(R.id.fragmentContainer, venueSearchResultFragment, "VENUES_SEARCH_RESULT")
            ft.addToBackStack("VENUES_SEARCH_RESULT")
            ft.commit()
        }

        val priceRange = view.findViewById<RangeBar>(R.id.rbPriceCategory)
        val rgVenueType1 = view.findViewById<RadioGroup>(R.id.rgVenueType1)
        val rgVenueType2 = view.findViewById<RadioGroup>(R.id.rgVenueType2)
        rgVenueType2.setOnCheckedChangeListener { group, id -> rg1Check(group, id) }
        rgVenueType1.setOnCheckedChangeListener { group, id -> rg2Check(group, id) }
        priceRange.setOnRangeBarChangeListener{ _, _, _, leftPinValue, rightPinValue ->
            price = "$leftPinValue,$rightPinValue"
        }
    }

    private fun changeScope(group: RadioGroup, radioButtonId: Int){
        val selectedRadioButton = group.findViewById<RadioButton>(radioButtonId)
        when (selectedRadioButton.text){
            resources.getString(R.string.restaurants) -> scope = FOOD
            resources.getString(R.string.bars) -> scope = DRINKS
            resources.getString(R.string.coffee) -> scope = COFFEE
            resources.getString(R.string.arts) -> scope = ARTS
            resources.getString(R.string.shops) -> scope = SHOPS
            resources.getString(R.string.outdoors) -> scope = OUTDOORS
            resources.getString(R.string.sights) -> scope = SIGHTS
            resources.getString(R.string.trending) -> scope = TRENDING
            resources.getString(R.string.top) -> scope = TOP_PICKS
        }
    }

    private fun rg1Check(group: RadioGroup, id: Int) {
        changeScope(group, id)
        rgVenueType1.setOnCheckedChangeListener(null)
        rgVenueType1.clearCheck()
        rgVenueType1.setOnCheckedChangeListener { newGroup, newId -> rg2Check(newGroup, newId) }
    }

    private fun rg2Check(group: RadioGroup, id: Int) {
        changeScope(group, id)
        rgVenueType2.setOnCheckedChangeListener(null)
        rgVenueType2.clearCheck()
        rgVenueType2.setOnCheckedChangeListener { newGroup, newId -> rg1Check(newGroup, newId) }
    }

}