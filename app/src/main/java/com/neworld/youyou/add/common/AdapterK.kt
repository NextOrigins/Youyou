package com.neworld.youyou.add.common

import android.view.View
import android.view.ViewGroup


/**
 * @author by user on 2017/12/25.
 */
class
AdapterK<T>(bind: (Holder, MutableList<T>, Int) -> Unit, id: Int, list: ArrayList<T>)
	: Adapter<T>(list) {

    private var mHeadView: View? = null
	private var mFootView: View? = null

    companion object {
        private val TYPE_NORMAL = 0
        private val TYPE_HEADER = 1
	    private val TYPE_FOOTER = 2
    }

	init {
		val obs = object : AdapterObs<T> {
			override fun onBind(holder: Holder, bean: MutableList<T>, position: Int) {
                if (getItemViewType(position) == TYPE_HEADER
		                || getItemViewType(position) == TYPE_FOOTER) return

                val index = if (mHeadView != null) position - 1 else position
                bind.invoke(holder, bean, index)
            }

			override fun layoutId() = id
		}
		super.setObs(obs)
	}

    fun setHeadView(view: View) { // HeadView .
        mHeadView = view
        notifyItemInserted(0)
    }
	
	fun setFootView(view: View) {
		mFootView = view
		notifyItemInserted(itemCount)
	}

    fun getHeadView() = mHeadView
	
	fun getFootView() = mFootView

    fun addData(list: List<T>) {
        bean.addAll(list)
    }

    fun addDataAndClear(list: List<T>) {
        bean.clear()
        bean.addAll(list)
    }
	
	fun addDataToTop(list: ArrayList<T>) {
		list.addAll(bean)
		bean.clear()
		bean.addAll(list)
	}

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && mHeadView != null) return TYPE_HEADER
	    if (position == itemCount - 1 && mFootView != null) return TYPE_FOOTER
        return TYPE_NORMAL
    }
	
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        return if (mHeadView != null && viewType == TYPE_HEADER ) Holder(mHeadView)
        else if (mFootView != null && viewType == TYPE_FOOTER) Holder(mFootView)
        else super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount()
            = if (mHeadView == null) super.getItemCount() else super.getItemCount() + 1
    
}