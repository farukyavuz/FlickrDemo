package org.faruk.flickr.ui.main

import org.faruk.flickr.model.Photos

interface MainView {
    fun showWait()

    fun removeWait(mError: Boolean)

    fun onFailure(mErrorMessage: String)

    fun onFlickListSuccess(mFlickrPhotos: Photos?, mFromPagination: Boolean = false)

    fun onRefreshFinish()
}