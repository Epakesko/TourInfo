package hu.bme.aut.android.tourinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import hu.bme.aut.android.tourinfo.listeners.FileMethodsListener
import kotlinx.android.synthetic.main.city_selection.*


class CitySelectionFragment : DialogFragment() {
    private lateinit var fileMethodsListener: FileMethodsListener
    private lateinit var selectedPlace: Place
    private lateinit var latLng: String

    companion object {
        const val FILEPATH = "filePath"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fileMethodsListener = activity as FileMethodsListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.city_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typeFilter = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build()
        val autocompleteFragment = fragmentManager!!.findFragmentById(R.id.place_autocomplete_fragment) as SupportPlaceAutocompleteFragment
        autocompleteFragment.setFilter(typeFilter)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedPlace = place
                latLng = selectedPlace.latLng.latitude.toString() + "," + selectedPlace.latLng.longitude.toString()

                btnSelectCity.visibility = View.VISIBLE
            }

            override fun onError(status: Status) {
                Toast.makeText(activity, "Error in autocomplete fragment: $status", Toast.LENGTH_SHORT).show()
            }
        })

        autocompleteFragment.view?.findViewById<View>(R.id.place_autocomplete_clear_button)?.setOnClickListener { view ->
            autocompleteFragment.setText("")
            view.visibility = View.GONE
            btnSelectCity.visibility = View.GONE
        }

        btnSelectCity.setOnClickListener {
            val intent = Intent(activity, VenuesActivity::class.java)
            intent.putExtra(FILEPATH, fileMethodsListener.createFile(selectedPlace.address as String, latLng))

            startActivity(intent)
            onDestroyView()
            //this.dismiss()
            Toast.makeText(activity, "New itinerary created!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        destroyFragment()
        super.onDestroyView()
    }

    private fun destroyFragment(){
        val fragment = fragmentManager!!.findFragmentById(R.id.place_autocomplete_fragment) as SupportPlaceAutocompleteFragment
        (fragmentManager as FragmentManager).beginTransaction().remove(fragment).commit()
    }
}