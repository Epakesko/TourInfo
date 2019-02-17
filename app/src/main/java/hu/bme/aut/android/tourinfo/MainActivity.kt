package hu.bme.aut.android.tourinfo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import hu.bme.aut.android.tourinfo.listeners.FileMethodsListener
import hu.bme.aut.android.tourinfo.model.Itinerary
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity(), FileMethodsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNewItinerary.setOnClickListener {
            val todoCreateFragment = CitySelectionFragment()
            todoCreateFragment.show(supportFragmentManager, "CitySelection")
        }

        btnLoadItinerary.setOnClickListener {
            val todoCreateFragment = LoadItineraryFragment()
            todoCreateFragment.show(supportFragmentManager, "FileSelection")
        }
    }

    override fun getFiles(): MutableList<HashMap<String, String>>{
        val directory = File(filesDir, "cities")

        val listOfFiles: MutableList<HashMap<String, String>> = mutableListOf()

        directory.list()?.forEach{ cityName ->
            val cityDir = File(directory, cityName)
            cityDir.list()?.forEach { fileName ->
                val fileMap = HashMap<String, String>()
                fileMap["city"] = cityName
                fileMap["date"] = fileName
                fileMap["path"] = File(cityDir, fileName).path
                listOfFiles.add(fileMap)
            }
        }

        return listOfFiles
    }

    override fun createFile(address: String, latLng: String): String{
        var gson = Gson()
        val directory = File(filesDir, "cities" + File.separator + address)
        directory.mkdirs()
        val format = SimpleDateFormat("yyyy.MM.dd-HH:mm:ss")
        val currentDate = format.format(Date())

        val itinerary = Itinerary(currentDate, mutableListOf(), mutableListOf(), address, latLng, mutableListOf())
        val file = File(directory, currentDate.toString() + ".json")
        file.createNewFile()
        val a = gson.toJson(itinerary)
        file.writeText(a)
        return file.path
    }

    override fun removeFile(filePath: String) {
        File(filePath).delete()
    }
}
