package hu.bme.aut.android.tourinfo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hu.bme.aut.android.tourinfo.R
import hu.bme.aut.android.tourinfo.model.Day
import kotlinx.android.synthetic.main.row_venue.view.*
import kotlinx.android.synthetic.main.row_distance.view.*

class ItineraryAdapter(private val day: Day) : RecyclerView.Adapter<ItineraryAdapter.ViewHolder>() {
    private val VENUE = 1
    private val ARROW = 2


    var itemClickListener: VenueItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType == VENUE){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_venue, parent, false)
            ViewHolderVenue(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_distance, parent, false)
            ViewHolderDistance(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position.rem(2) == 1) ARROW else VENUE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder.itemViewType == VENUE){
            val venue = day.venues[position / 2]
            (holder as ViewHolderVenue).venueId = venue.id
            holder.tvName.text = venue.name
            holder.tvLocation.text = venue.location.formattedAddress.joinToString(", ")
        }
        else{
            val matrixElement = day.distances[position / 2]
            (holder as ViewHolderDistance).tvDistance.text = matrixElement.distance.text
            holder.tvDuration.text = matrixElement.duration.text
        }
    }

    override fun getItemCount(): Int{
        return day.venues.size + day.distances.size
    }

    abstract inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolderVenue(itemView: View) : ViewHolder(itemView) {
        val tvName: TextView = itemView.tvName
        val tvLocation: TextView = itemView.tvLocation
        var venueId: String? = null

        init {
            itemView.setOnClickListener {
                venueId?.let { venueId -> itemClickListener?.onItemClick(venueId) }
            }
        }
    }

    inner class ViewHolderDistance(itemView: View) : ViewHolder(itemView) {
        val tvDistance: TextView = itemView.tvDistance
        val tvDuration: TextView = itemView.tvDuration
    }

    interface VenueItemClickListener {
        fun onItemClick(venueId: String)
    }

}

