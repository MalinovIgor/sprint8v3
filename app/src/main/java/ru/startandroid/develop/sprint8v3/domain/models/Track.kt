package ru.startandroid.develop.sprint8v3.domain.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


const val replacementForBigImg = "512x512bb.jpg"
const val delimiterForBigImg = "/"
data class Track (
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    val artworkUrl100: String,
    val trackId : String,
    val collectionName : String?,
    val releaseDate : String?,
    val primaryGenreName : String?,
    val country : String?,
    val previewUrl : String?
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast(delimiterForBigImg, replacementForBigImg)

}