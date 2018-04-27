package com.neworld.youyou.view.mview.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.neworld.youyou.R
/**
 * @author by hhhh on 2018/4/16.
 */
class HorizontalDecoration
(context: Context, height: Int,
 color: Int = ActivityCompat.getColor(context, R.color.line_bg)) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint
    private var mDividerHeight = 0

    init {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color

        mPaint = paint
        mDividerHeight = height
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent != null && outRect != null && parent.getChildAdapterPosition(view) != 0) {
            outRect.top = mDividerHeight
        }
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)

        if (parent != null && c != null) {
            for (i in 0 until parent.childCount) {
                val view = parent.getChildAt(i)
                val index = parent.getChildAdapterPosition(view)

                if (index == 0) {
                    continue
                }

                val left = parent.paddingLeft.toFloat()
                val top = view.top.toFloat()
                val right = parent.width - parent.paddingRight.toFloat()
                val btm = view.top - mDividerHeight.toFloat()

                c.drawRect(left, top, right, btm, mPaint)
            }
        }
    }
}