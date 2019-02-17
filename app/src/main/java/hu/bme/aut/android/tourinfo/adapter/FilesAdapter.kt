package hu.bme.aut.android.tourinfo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hu.bme.aut.android.tourinfo.R
import kotlinx.android.synthetic.main.row_file.view.*

class FilesAdapter(private val fileList: MutableList<HashMap<String, String>>) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    var itemClickListener: FileClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = fileList[position]

        holder.filePath = file["path"]
        holder.tvCity.text = file["city"]
        holder.tvDate.text = file["date"]
    }

    fun deleteRow(position: Int) {
        fileList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount() = fileList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCity: TextView = itemView.tvCity
        val tvDate: TextView = itemView.tvDate
        var filePath: String? = null

        init {
            itemView.setOnClickListener {
                filePath?.let { filePath -> itemClickListener?.onItemClick(filePath) }
            }
            itemView.setOnLongClickListener {
                filePath?.let { filePath -> itemClickListener?.onItemLongClick(filePath, adapterPosition, it) }
                true
            }
        }
    }

    interface FileClickListener {
        fun onItemClick(filePath: String)
        fun onItemLongClick(filePath: String, position: Int, view: View)
    }

}

