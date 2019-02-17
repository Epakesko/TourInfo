package hu.bme.aut.android.tourinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import hu.bme.aut.android.tourinfo.adapter.FilesAdapter
import hu.bme.aut.android.tourinfo.listeners.FileMethodsListener
import kotlinx.android.synthetic.main.list_itineraries.*

class LoadItineraryFragment : DialogFragment(), FilesAdapter.FileClickListener {
    private lateinit var fileMethodsListener: FileMethodsListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fileMethodsListener = activity as FileMethodsListener
    }

    override fun onItemClick(filePath: String) {
        val intent = Intent(activity, VenuesActivity::class.java)
        intent.putExtra(CitySelectionFragment.FILEPATH, filePath)
        startActivity(intent)
        Toast.makeText(activity, "Itinerary loaded!", Toast.LENGTH_LONG).show()
    }

    override fun onItemLongClick(filePath: String, position: Int, view: View) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.file_todo)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete ->{
                    (rvItineraries.adapter as FilesAdapter).deleteRow(position)
                    fileMethodsListener.removeFile(filePath)
                }
            }
            false
        }
        popup.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_itineraries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvItineraries.layoutManager = LinearLayoutManager(activity)

        rvItineraries.adapter = FilesAdapter(fileMethodsListener.getFiles())
        (rvItineraries.adapter as FilesAdapter).itemClickListener = this
    }
}