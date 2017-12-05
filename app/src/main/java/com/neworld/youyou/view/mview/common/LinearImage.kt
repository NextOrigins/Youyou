package com.neworld.youyou.view.mview.common

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * @author by user on 2017/11/30.
 */
class LinearImage
@JvmOverloads
constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attr, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val vgWidth = MeasureSpec.getSize(widthMeasureSpec)
        val vgHeight = MeasureSpec.getSize(heightMeasureSpec)
        val vgMode = MeasureSpec.getMode(heightMeasureSpec)
        var height = paddingTop + paddingBottom

        (0 until childCount)
                .map { getChildAt(it) }
                .forEach {
                    measureChild(it, widthMeasureSpec, heightMeasureSpec)
                    height += it.measuredHeight
                }

        setMeasuredDimension(vgWidth, if (vgMode == MeasureSpec.EXACTLY) vgHeight else height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var height = paddingTop
        val left = paddingLeft
        val right = r - paddingRight

        (0 until childCount)
                .map { getChildAt(it) }
                .forEach {
                    val spaceHeight = it.measuredHeight

                    val top = height
                    val bottom = top + spaceHeight

                    it.layout(left, top, right, bottom)

                    height += spaceHeight
                }
    }
}