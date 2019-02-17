package hu.bme.aut.android.tourinfo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hu.bme.aut.android.tourinfo.R
import hu.bme.aut.android.tourinfo.model.Venue
import kotlinx.android.synthetic.main.row_venue.view.*

class VenuesAdapter(private val venueList: MutableList<Venue>) : RecyclerView.Adapter<VenuesAdapter.ViewHolder>() {

    var itemClickListener: VenueItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_venue, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val venue = venueList[position]

        holder.venueId = venue.id
        holder.tvName.text = venue.name
        holder.tvLocation.text = venue.location.formattedAddress.joinToString(", ")
    }

    override fun getItemCount() = venueList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tvName
        val tvLocation: TextView = itemView.tvLocation
        var venueId: String? = null

        init {
            itemView.setOnClickListener {
                venueId?.let { venueId -> itemClickListener?.onItemClick(venueId) }
            }
        }
    }

    interface VenueItemClickListener {
        fun onItemClick(venueId: String)
    }

}

