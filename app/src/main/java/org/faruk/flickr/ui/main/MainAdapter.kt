package org.faruk.flickr.ui.main


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.*

import androidx.recyclerview.widget.RecyclerView
import org.faruk.flickr.R
import org.faruk.flickr.model.FlickParams
import org.faruk.flickr.model.Photo
import org.faruk.flickr.ui.detail.DetailActivity
import org.faruk.flickr.util.DETAIL_DATA
import org.faruk.flickr.util.TYPE_CARD
import org.faruk.flickr.util.TYPE_FOOTER
import org.faruk.flickr.util.loadFromFlickr
import kotlin.properties.Delegates

class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: MutableList<Photo> by Delegates.observable(
        mutableListOf()
    ) { _, _, _ -> notifyDataSetChanged() }

    override fun getItemCount() = items.count()

    override fun getItemViewType(position: Int): Int {
        return items[position].mViewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderCard -> {
                holder.bindView(items[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mInflater = LayoutInflater.from(parent.context)
        val mViewHolder: RecyclerView.ViewHolder
        when (viewType) {
            TYPE_CARD -> {
                val v = mInflater.inflate(R.layout.item_card, parent, false)
                mViewHolder = ViewHolderCard(v)
            }
            TYPE_FOOTER -> {
                val v = mInflater.inflate(R.layout.item_progress, parent, false)
                mViewHolder = ViewHolderFooter(v)
            }
            else -> {
                val v = mInflater.inflate(R.layout.item_card, parent, false)
                mViewHolder = ViewHolderCard(v)
            }
        }

        return mViewHolder
    }

    class ViewHolderFooter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.findViewById(R.id.itemProgressBar) as ProgressBar
    }

    class ViewHolderCard(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTxtName: TextView = itemView.findViewById(R.id.itemCardUsername) as TextView
        var mTxtTitle: TextView = itemView.findViewById(R.id.itemCardTitle) as TextView
        var mTxtDate: TextView = itemView.findViewById(R.id.itemCardDate) as TextView
        var mImgUser: ImageView = itemView.findViewById(R.id.itemCardUserImage) as ImageView
        var mImgMain: ImageView = itemView.findViewById(R.id.itemCardImage) as ImageView

        fun bindView(mPhoto: Photo) {
            if (mPhoto.url_s.isNotEmpty()) {
                this.mImgMain.loadFromFlickr(mPhoto.url_s)
            } else {
                this.mImgMain.loadFromFlickr(
                    FlickParams(
                        mPhoto.farm,
                        mPhoto.server,
                        mPhoto.id,
                        mPhoto.secret
                    ).toString()
                )
            }
            mTxtName.text = mPhoto.ownername
            mTxtTitle.text = if (mPhoto.title.length > 30) "${mPhoto.title.substring(0, 30)}..." else mPhoto.title
            mTxtDate.text = mPhoto.getDateFormated()
            mImgUser.loadFromFlickr(mPhoto.getUserPhotoUrl(), true)

            itemView.setOnClickListener {

                val mIntentDetail = Intent(itemView.context, DetailActivity::class.java)
                mIntentDetail.putExtra(DETAIL_DATA, mPhoto)

                val mPairImg: Pair<View, String> = Pair(
                    this.mImgMain,
                    itemView.context.getString(R.string.transition_image)
                )

                val mPairTitle: Pair<View, String> =
                    Pair(this.mTxtTitle, itemView.context.getString(R.string.transition_title))

                val mPairUserPic: Pair<View, String> =
                    Pair(this.mImgUser, itemView.context.getString(R.string.transition_image_user))

                val mOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity, mPairImg, mPairTitle, mPairUserPic
                )

                ActivityCompat.startActivity(
                    itemView.context as Activity, mIntentDetail, mOptions.toBundle()
                )
            }
        }

    }
}