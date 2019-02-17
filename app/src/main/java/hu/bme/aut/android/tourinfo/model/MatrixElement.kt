package hu.bme.aut.android.tourinfo.model

data class MatrixElement (
    val status: String,
    val duration: MatrixDuration,
    val distance: MatrixDistance
)