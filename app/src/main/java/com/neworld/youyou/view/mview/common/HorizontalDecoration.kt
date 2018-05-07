package com.neworld.youyou.view.mview.common

import android.content.Context
import android.graphics.Canvas
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

        outRect?.set(0, 0, 0, mDividerHeight)
        /*if (parent != null && outRect != null && parent.getChildAdapterPosition(view) != 0) {
            outRect.top = mDividerHeight
        }*/
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        if (parent != null) {
            val l = parent.paddingLeft.toFloat()
            val r = (parent.width - parent.paddingRight).toFloat()

            for (i in 0 until parent.childCount) {
                val v = parent.getChildAt(i)

                val p = v.layoutParams as RecyclerView.LayoutParams

                val t = (v.bottom + p.bottomMargin).toFloat()
                val b = t + mDividerHeight

                c?.drawRect(l, t, r, b, mPaint)
            }
        }

        /*if (parent != null && c != null) {
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
        }*/
    }
}