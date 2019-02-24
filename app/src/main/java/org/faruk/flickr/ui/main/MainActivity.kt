package org.faruk.flickr.ui.main

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.faruk.flickr.R
import org.faruk.flickr.base.BaseActivity
import org.faruk.flickr.model.Photo
import org.faruk.flickr.model.Photos
import org.faruk.flickr.util.EndlessRecyclerViewScrollListener
import org.faruk.flickr.util.ErrorView
import org.faruk.flickr.util.init

class MainActivity : BaseActivity(), MainView {

    var mAdapter = MainAdapter()
    var mPresenter: MainPresenter? = null
    var mScrollListener: EndlessRecyclerViewScrollListener? = null
    var mPage = 1
    var mFlickPhotos: MutableList<Photo> = arrayListOf()
    var mCurrentLayout = 2

    override fun showWait() {
        mProgressBar.visibility = View.VISIBLE
        mLinearError.visibility = View.GONE
        mRecycler.visibility = View.GONE
    }

    override fun removeWait(mError: Boolean) {
        mProgressBar.visibility = View.GONE
        mSwipeFrag.isRefreshing = false
        if (mError) {
            mLinearError.visibility = View.VISIBLE
            mRecycler.visibility = View.GONE
        } else {
            mLinearError.visibility = View.GONE
            mRecycler.visibility = View.VISIBLE
        }
    }

    override fun onFailure(mErrorMessage: String) {
        mLinearError.removeAllViews()
        mLinearError.addView(ErrorView(this).init(mErrorMessage))
    }

    override fun onFlickListSuccess(mFlickrPhotos: Photos?, mFromPagination: Boolean) {
        if (mFlickrPhotos!!.total > 0) {
            if (mFromPagination) {
                val mSize = mAdapter.items.size
                mAdapter.items.removeAt(mSize - 1)
                mAdapter.items.addAll(mFlickrPhotos.photo as MutableList<Photo>)
                mFlickPhotos = mAdapter.items
                mRecycler.adapter?.notifyItemRangeChanged(mSize - 1, mAdapter.items.size - mSize);
            } else {
                mScrollListener?.resetState();
                mAdapter.items = mFlickrPhotos.photo as MutableList<Photo>
                mFlickPhotos = mAdapter.items
            }
        }
    }

    override fun onRefreshFinish() {
        mSwipeFrag.isRefreshing = false
        mAdapter.items.forEach { it.mViewType = mCurrentLayout }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        if (savedInstanceState == null) {
            mRecycler.init(RecyclerView.VERTICAL)
            mRecycler.adapter = mAdapter
            addRecyclerListener()
        }

        mPresenter = MainPresenter(this, this)
        mPresenter?.getFlickPhotos()
        mSwipeFrag.setOnRefreshListener {
            mPresenter?.getFlickPhotos(mCurrentLayout)
        }
    }

    fun addRecyclerListener() {
        mScrollListener = object : EndlessRecyclerViewScrollListener(
            mRecycler.layoutManager as LinearLayoutManager
        ) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (!hasFooter()) {
                    val mHandler = Handler()
                    mHandler.post {
                        mAdapter.items.add(Photo(mViewType = 2))
                        mRecycler.adapter?.notifyItemInserted(mAdapter.items.size - 1)
                    }

                    mPage++
                    mPresenter?.getFlickPhotos(
                        mPage = mPage, mFromPagination = true,
                        mViewType = mCurrentLayout
                    )
                }
            }
        }
        mRecycler.addOnScrollListener(mScrollListener as EndlessRecyclerViewScrollListener)
    }

    private fun hasFooter(): Boolean {
        return mAdapter.items.get(mAdapter.items.size - 1).mViewType == 3;
    }
}