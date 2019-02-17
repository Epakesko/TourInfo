package hu.bme.aut.android.tourinfo.model

data class Day (
        val venues: MutableList<Venue>,
        val distances: MutableList<MatrixElement>
)
