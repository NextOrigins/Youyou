package com.neworld.youyou.add.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * @author by user on 2017/12/25.
 * 带有HeaderView 和 FooterView的Adapter (Kotlin用)
 */
class
AdapterK<T>(bind: (Holder, MutableList<T>, Int) -> Unit,
            private val id: Array<Int>, list: ArrayList<T>,
            private val type: (T) -> Int = { TYPE_NORMAL })
    : Adapter<T>(list) {

    var headView: View? = null
        set(value) {
            field = value; notifyItemInserted(0)
        }

    var footView: View? = null
        set(value) {
            field = value; notifyItemInserted(itemCount - 1)
        }

    companion object {
        private const val TYPE_HEADER = 66
        private const val TYPE_FOOTER = 77
        const val TYPE_NORMAL = 0
        const val TYPE_ONE = 1
        const val TYPE_TWO = 2
    }

    init {
        val obs = object : AdapterObs<T> {
            override fun onBind(holder: Holder, bean: MutableList<T>, position: Int) {
                if (getItemViewType(position) == TYPE_HEADER
                        || getItemViewType(position) == TYPE_FOOTER) return

                val index = if (headView != null) position - 1 else position
                bind.invoke(holder, bean, index)
            }

            override fun layoutId() = id[0]
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

        bean.add(0, t)
        notifyItemInserted(index)

        val count = if (footView != null) itemCount - 1 else itemCount

        if (index < count)
            notifyItemRangeChanged(index, count)
    }

    fun getSize() = bean.size

    override fun remove(position: Int) {
        if (headView == null && footView == null) {
            super.remove(position)
            return
        }

        bean.removeAt(position)
        notifyItemRemoved(if (headView != null) position + 1 else position)

        notifyDataSetChanged()

        /*if (position < bean.size)
            notifyItemRangeChanged(1, bean.size)*/
    }

    override fun getItemViewType(position: Int) = when {
        position == 0 && headView != null -> TYPE_HEADER
        position == itemCount - 1 && footView != null -> TYPE_FOOTER
        else -> type.invoke(bean[if (headView != null) position - 1 else position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when {
        headView != null && viewType == TYPE_HEADER -> Holder(headView)
        footView != null && viewType == TYPE_FOOTER -> Holder(footView)
        viewType == TYPE_ONE || viewType == TYPE_TWO -> Holder(LayoutInflater.from(parent.context)
                .inflate(id[viewType], parent, false))
        else -> super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount() = when {
        headView != null && footView != null -> super.getItemCount() + 2
        headView != null || footView != null -> super.getItemCount() + 1
        else -> super.getItemCount()
    }
}