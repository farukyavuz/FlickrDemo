package org.faruk.flickr.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import org.faruk.flickr.R

class ErrorView(val mContext : Context) {
    fun init(mMessage: String = mContext.getString(R.string.error_empty_data)): View {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.error_view, null)
        val mText = mView.findViewById(R.id.mTxtMessageStatus) as TextView
        mText.text = mMessage
        return mView
    }
}