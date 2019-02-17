package hu.bme.aut.android.tourinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import hu.bme.aut.android.tourinfo.listeners.VenueMethodsListener
import hu.bme.aut.android.tourinfo.model.Venue
import hu.bme.aut.android.tourinfo.model.VenueDetailsEvent
import hu.bme.aut.android.tourinfo.network.FourSquarePlacesInteractor
import kotlinx.android.synthetic.main.venue_details.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class VenueDetailsFragment : Fragment(){
    private lateinit var venueMethodsListener: VenueMethodsListener
    private val placesInteractor = FourSquarePlacesInteractor()
    private lateinit var detailedVenue: Venue

    companion object {

        fun newInstance(venueId: String, isRestaurant: Boolean = false) = VenueDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(VenueFragment.VENUE_ID, venueId)
                putBoolean(VenueFragment.IS_REST, isRestaurant)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        venueMethodsListener = activity as VenueMethodsListener
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
        return inflater.inflate(R.layout.venue_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val venueId = arguments!!.getString(VenueFragment.VENUE_ID) as String
        val isRestaurant = arguments!!.getBoolean(VenueFragment.IS_REST)
        val venue = venueMethodsListener.getVenue(venueId)

        if(venue != null){
            detailedVenue = venue
            btnSelectVenue.text = resources.getString(R.string.already_selected)
            btnSelectVenue.isClickable = false
            loadDetailedVenueIntoView()
        }
        else{
            placesInteractor.getVenueDetails(
                    venueId = venueId
            )
            btnSelectVenue.text = resources.getString(R.string.add_to_selection)
            btnSelectVenue.isClickable = true
            btnSelectVenue.setOnClickListener {
                if(isRestaurant) venueMethodsListener.addRestaurant(detailedVenue) else venueMethodsListener.addVenue(detailedVenue)
                btnSelectVenue.text = resources.getString(R.string.already_selected)
                btnSelectVenue.isClickable = false
            }
        }
    }

    private fun loadDetailedVenueIntoView(){
        progressBar.visibility = View.GONE
        btnSelectVenue.visibility = View.VISIBLE

        tvName.visibility = View.VISIBLE
        tvNameLabel.visibility = View.VISIBLE
        tvName.text = detailedVenue.name

        tvDescription.visibility = View.VISIBLE
        tvDescriptionLabel.visibility = View.VISIBLE
        tvDescription.text = detailedVenue.description

        tvCategories.visibility = View.VISIBLE
        tvCategoriesLabel.visibility = View.VISIBLE
        tvCategories.text = detailedVenue.categories.joinToString(separator = ", ", transform = { it.name })

        tvRating.visibility = View.VISIBLE
        tvRatingLabel.visibility = View.VISIBLE
        tvRating.text = detailedVenue.rating.toString()

        if(detailedVenue.url != null){
            tvUrl.visibility = View.VISIBLE
            tvUrlLabel.visibility = View.VISIBLE
            tvUrl.text = detailedVenue.url
        }
        if(detailedVenue.price != null){
            tvPrice.visibility = View.VISIBLE
            tvPriceLabel.visibility = View.VISIBLE
            tvPrice.text = detailedVenue.price.message
        }
        if(detailedVenue.hours != null){
            tvHoursLabel.visibility = View.VISIBLE
            tvHours.visibility = View.VISIBLE
            tvHours.text = detailedVenue.hours.status
        }

        if(detailedVenue.bestPhoto != null){
            Glide.with(this)
                    .load("${detailedVenue.bestPhoto.prefix}${detailedVenue.bestPhoto.width}x${detailedVenue.bestPhoto.height}${detailedVenue.bestPhoto.suffix}")
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(ivPhoto)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSuccessEvent(event: VenueDetailsEvent) {
        val venue = event.response.response.venue
        detailedVenue = venue
        loadDetailedVenueIntoView()
    }
}