package org.faruk.flickr.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.loadFromFlickr(mFlickUrl: String, mCircle: Boolean = false) {

    val mPicasso = Picasso.with(this.context)
        .load(mFlickUrl)
    if (mCircle) {
        mPicasso.fit()
        mPicasso.transform(CircleTransform(this.context, false))
    }

    mPicasso.into(this)
}

fun RecyclerView.init(mOrientation: Int = RecyclerView.VERTICAL) {
    this.setHasFixedSize(true)
    val llm = LinearLayoutManager(this.context)
    llm.orientation = mOrientation
    this.layoutManager = llm
    this.itemAnimator = DefaultItemAnimator()
}

fun Context.isConnectingToInternet(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as
            ConnectivityManager
    return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
}

fun retrofit(mUrl: String = BASE_URL): Retrofit {
    val mRetrofit = Retrofit.Builder()
        .baseUrl(mUrl)
        .addConverterFactory(GsonConverterFactory.create())
    val httpClient = OkHttpClient.Builder()
    mRetrofit.client(httpClient.addInterceptor { chain ->
        val mOriginal = chain.request()
        val mUrl = mOriginal.url().newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()
        val requestBuilder = mOriginal.newBuilder()
            .url(mUrl)
        chain.proceed(requestBuilder.build())
    }.build())

    return mRetrofit.build()
}

fun Date.parseToFormat(mFormat: String): String {
    val mOutputFormat = SimpleDateFormat(mFormat, Locale("tr_TR"))
    return mOutputFormat.format(this)
}