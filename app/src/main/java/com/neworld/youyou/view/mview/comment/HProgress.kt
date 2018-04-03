package com.neworld.youyou.view.mview.comment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * @author by hhhh on 2018/3/27.
 * 用作WebView的进度条;
 */
class HProgress
@JvmOverloads
constructor(context: Context, attr: AttributeSet? = null, def: Int = 0)
    : View(context, attr, def) {

    private val mPaint by lazy {
        val paint = Paint()
        paint.isDither = true
        paint.isAntiAlias = true
        paint.strokeWidth = 10f
        paint.color = Color.RED
        paint
    }
    private var mHeight = 0
    private var mWidth = 0
    var newProgress = 0f
        set(value) { field = value; invalidate() }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mHeight = h
        mWidth = w
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, mWidth * newProgress / 100, mWidth.toFloat(), mPaint)
        super.onDraw(canvas)
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }
}