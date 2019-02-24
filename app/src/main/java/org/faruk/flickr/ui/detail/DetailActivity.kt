package org.faruk.flickr.ui.detail

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.*

import kotlinx.android.synthetic.main.activity_detail.*
import org.faruk.flickr.R
import org.faruk.flickr.base.BaseActivity
import org.faruk.flickr.model.FlickParams
import org.faruk.flickr.model.Photo
import org.faruk.flickr.ui.image.ImageFullActivity
import org.faruk.flickr.util.DETAIL_DATA
import org.faruk.flickr.util.loadFromFlickr

class DetailActivity : BaseActivity() {

    private var mPhoto: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp)
        mToolbar.setNavigationOnClickListener {
            supportFinishAfterTransition()
        }

        mPhoto = intent.getParcelableExtra(DETAIL_DATA)

        mPhoto.let {

            mCollapsingDetail.title = it?.title

            if (it?.url_s != null && it.url_s.isNotEmpty()) {
                mImgDetail.loadFromFlickr(it.url_s)
            } else {
                mImgDetail.loadFromFlickr(
                    FlickParams(it?.farm!!, it.server, it.id, it.secret).toString()
                )
            }

            itemCardUsername.text = it.ownername
            itemCardTitle.text = it.title
            mTxtFlickDescription.text = Html.fromHtml(it.description?._content)
            itemCardUserImage.loadFromFlickr(it.getUserPhotoUrl(), true)
            itemCardDate.text = it.getDateFormated()

            mImgDetail.setOnClickListener {

                val mIntentDetail = Intent(this, ImageFullActivity::class.java)
                mIntentDetail.putExtra(DETAIL_DATA, mPhoto)

                val mPairImg: Pair<View, String> = Pair(this.mImgDetail, getString(R.string.transition_image))

                val mOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    mPairImg
                )

                ActivityCompat.startActivity(this, mIntentDetail, mOptions.toBundle())
            }
        }

    }
}