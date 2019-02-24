package org.faruk.flickr.ui.main

import android.content.Context
import org.faruk.flickr.R
import org.faruk.flickr.model.RecentResponse
import org.faruk.flickr.network.FlickrApi
import org.faruk.flickr.util.TYPE_CARD
import org.faruk.flickr.util.isConnectingToInternet
import org.faruk.flickr.util.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainPresenter(val mMainView: MainView, val mContext: Context) {

    val api = retrofit().create(FlickrApi::class.java)

    fun getFlickPhotos(
        mPage: Int = 1, mPerpage: Int = 30,
        mFromPagination: Boolean = false,
        mViewType: Int = TYPE_CARD
    ) {
        if (!mFromPagination) {
            mMainView.showWait()
        }

        if (mContext.isConnectingToInternet()) {
            val mCall = api.getRecent(mPage, mPerpage)
            mCall.enqueue(object : Callback<RecentResponse> {
                override fun onResponse(call: Call<RecentResponse>?, response: Response<RecentResponse>?) {
                    var mError = false
                    try {
                        if (response?.body() != null) {
                            mMainView.onFlickListSuccess(response.body()?.photos, mFromPagination)
                            response.body()?.photos?.photo?.forEach { it.mViewType = mViewType }
                        }
                    } catch (e: Exception) {
                        mMainView.onFailure("${e.message} .. ${e.cause}")
                        call?.cancel()
                        mError = true
                    }

                    mMainView.removeWait(mError)
                    mMainView.onRefreshFinish()
                }

                override fun onFailure(call: Call<RecentResponse>?, t: Throwable?) {
                    mMainView.removeWait(true)
                    mMainView.onFailure("${t?.message} .. ${t?.cause}")
                }
            })
        } else {
            mMainView.removeWait(true)
            mMainView.onFailure(mContext.getString(R.string.no_internet_connection))
        }
    }
}