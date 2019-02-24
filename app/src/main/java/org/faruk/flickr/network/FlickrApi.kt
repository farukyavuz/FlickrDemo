package org.faruk.flickr.network

import org.faruk.flickr.model.RecentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("?format=json&nojsoncallback=1&method=flickr.photos.getRecent&extras=date_upload,url_o,owner_name,description,url_m,url_s,icon_server,views")
    fun getRecent(@Query("page") page: Int = 1, @Query("per_page") per_page: Int = 50): Call<RecentResponse>
}