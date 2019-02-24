package org.faruk.flickr.ui.image

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_image_full.*
import org.faruk.flickr.R
import org.faruk.flickr.base.BaseActivity
import org.faruk.flickr.model.FlickParams
import org.faruk.flickr.model.Photo
import org.faruk.flickr.util.DETAIL_DATA
import org.faruk.flickr.util.loadFromFlickr

class ImageFullActivity : BaseActivity() {

    private var mPhoto: Photo? = null
    private val mHideHandler = Handler()

    private val mHidePart2Runnable = Runnable {
        mImgFullScreen!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private var mControlsView: View? = null

    private val mShowPart2Runnable = Runnable {
        val actionBar = supportActionBar
        actionBar?.show()
        mControlsView!!.visibility = View.VISIBLE
    }

    private var mVisible: Boolean = false

    private val mHideRunnable = Runnable { hide() }

    private val mDelayHideTouchListener = View.OnTouchListener { _, motionEvent ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_full)
        mVisible = true
        mControlsView = findViewById(R.id.fullContentControls)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        supportActionBar?.elevation = 0f

        mImgFullScreen.setOnClickListener { toggle() }

        mPhoto = intent.getParcelableExtra(DETAIL_DATA)

        mPhoto.let {
            if (it?.url_s?.isNotEmpty()!!) {
                mImgFullScreen.loadFromFlickr(it.url_s)
            } else {
                mImgFullScreen.loadFromFlickr(
                    FlickParams(
                        mPhoto?.farm!!, mPhoto?.server!!, mPhoto?.id!!,
                        mPhoto!!.secret
                    ).toString()
                )
            }

            fullUsername.text = mPhoto?.ownername
            fullTitle.text = mPhoto?.title
            mTxtFlickViews.text = getString(R.string.views, mPhoto?.views.toString())
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            supportFinishAfterTransition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        supportActionBar?.hide()
        mControlsView!!.visibility = View.GONE
        mVisible = false

        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        mImgFullScreen!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        private val AUTO_HIDE = true
        private val AUTO_HIDE_DELAY_MILLIS = 3000
        private val UI_ANIMATION_DELAY = 300
    }
}
