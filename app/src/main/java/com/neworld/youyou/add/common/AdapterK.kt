package com.neworld.youyou.add.common

import android.view.View
import android.view.ViewGroup


/**
 * @author by user on 2017/12/25.
 * 带有HeaderView 和 FooterView的Adapter (Kotlin用)
 */
class
AdapterK<T>(bind: (Holder, MutableList<T>, Int) -> Unit, id: Int, list: ArrayList<T>)
	: Adapter<T>(list) {

    var headView: View? = null
        set(value) { field = value; notifyItemInserted(0) }

    var footView: View? = null
        set(value) { field = value; notifyItemInserted(itemCount - 1)}

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_HEADER = 1
	    private const val TYPE_FOOTER = 2
    }

	init {
		val obs = object : AdapterObs<T> {
			override fun onBind(holder: Holder, bean: MutableList<T>, position: Int) {
                if (getItemViewType(position) == TYPE_HEADER
		                || getItemViewType(position) == TYPE_FOOTER) return

                val index = if (headView != null) position - 1 else position
                bind.invoke(holder, bean, index)
            }

			override fun layoutId() = id
		}
		super.setObs(obs)
	}

    fun addData(list: List<T>) {
        bean.addAll(list)
    }

    fun addDataAndClear(list: List<T>) {
        bean.clear()
        addData(list)
    }

	fun addDataToTop(list: ArrayList<T>) {
		list.addAll(bean)
		bean.clear()
        addData(list)
	}

    fun insertData(t: T) {
        val index = if (headView == null) 0 else 1
        val count = when {
            headView != null && footView != null -> itemCount - 2
            headView != null || footView != null -> itemCount - 1
            else -> itemCount
        }
        bean.add(0, t)
        notifyItemInserted(index)
        if (index < count - 1)
            notifyItemRangeInserted(index, count - 1) // TODO : why count size - 1 be correct ??
    }

    fun getSize(): Int = bean.size

    override fun remove(position: Int) {
        when {
            headView == null && footView == null -> super.remove(position)

            headView != null -> {
                val index = position + 1
                val count = if (footView != null) itemCount - 2 else itemCount - 1
                bean.removeAt(position)
                notifyItemRemoved(index)
                if (index < count)
                    notifyItemRangeChanged(index, count)
            }
        }
    }

    override fun getItemViewType(position: Int) = when {
        position == 0 && headView != null -> TYPE_HEADER
        position == itemCount - 1 && footView != null -> TYPE_FOOTER
        else -> TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = when {
        headView != null && viewType == TYPE_HEADER -> Holder(headView)
        footView != null && viewType == TYPE_FOOTER -> Holder(footView)
        else -> super.onCreateViewHolder(parent, viewType)
    }!!

    override fun getItemCount() = when {
	    headView != null && footView != null -> super.getItemCount() + 2
	    headView != null || footView != null -> super.getItemCount() + 1
	    else -> super.getItemCount()
    }
}