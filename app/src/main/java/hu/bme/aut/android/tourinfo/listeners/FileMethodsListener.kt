package hu.bme.aut.android.tourinfo.listeners

import java.util.HashMap

interface FileMethodsListener {
    fun getFiles(): MutableList<HashMap<String, String>>
    fun createFile(address: String, latLng: String): String
    fun removeFile(filePath: String)
}
