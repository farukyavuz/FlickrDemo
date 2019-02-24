package org.faruk.flickr.model

import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import org.faruk.flickr.util.parseToFormat
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class FlickParams(
    val farm_id: Int,
    val server_id: String,
    val id: String,
    val secret: String
) : Parcelable {
    override fun toString(): String {
        return "http://farm${farm_id}.static.flickr.com/${server_id}/${id}_${secret}.jpg"
    }
}

@Parcelize
data class Photo(
    val id: String = "",
    val owner: String = "",
    val secret: String = "",
    val title: String = "",
    val farm: Int = 0,
    val dateupload: String = "1000000000",
    val ownername: String = "",
    val url_o: String = "",
    val description: Description? = null,
    val server: String = "",
    val url_m: String = "",
    val url_s: String = "",
    var mViewType: Int = 1,
    val iconserver: String = "",
    val iconfarm: Int = 0,
    val views: Int = 0
) : Parcelable {

    fun getDateFormated(): String {
        try {
            Log.e("dateupload : ", dateupload)
            val mDate: Date? = Date(this.dateupload.toInt() * 1000L)
            val mInputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
            mInputFormat.timeZone = TimeZone.getTimeZone("UTC-3")
            val mDateOut = mInputFormat.parse(mInputFormat.format(mDate))
            return "${mDateOut.parseToFormat("d MMM")}, ${mDateOut.parseToFormat("yyyy")}"
        } catch (e: ParseException) {
            e.printStackTrace()
            return "NaN"
        }
    }

    fun getUserPhotoUrl(): String {
        return "http://farm${iconfarm}.staticflickr.com/${iconserver}/buddyicons/${owner}.jpg"
    }

    data class Description(val _content: String) : Serializable {}
}

@Parcelize
data class Photos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<Photo>
) : Parcelable

@Parcelize
data class RecentResponse(val photos: Photos) : Parcelable