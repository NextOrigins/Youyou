package com.neworld.youyou.add.common

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * @author by user on 2017/11/23.
 */
class PullToAdapter<T>(onUp: (Int) -> Unit, obs: AdapterObs<T>, list: List<T>) : Adapter<T>(obs, list) {

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                val lp = recyclerView?.layoutManager as GridLayoutManager
//                val last = lp.findLastVisibleItemPosition()
//                if (last == itemCount - 1)
//                    onUp(last)
                if (!recyclerView!!.canScrollVertically(1)) {
                    onUp(itemCount - 1)
                }
            }
        }
    }

    fun pullUpRefresh(recycler: RecyclerView) {
        recycler.addOnScrollListener(scrollListener)
    }

    fun removeListener(recycler: RecyclerView) {
        recycler.removeOnScrollListener(scrollListener)
    }
}