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
class WebProgress
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
    private var mProgress = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mHeight = h
        mWidth = w
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, mWidth * mProgress / 100, mWidth.toFloat(), mPaint)
        super.onDraw(canvas)
    }

    fun setProgress(newProgress: Int) {
        mProgress = newProgress.toFloat()
        invalidate()
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }
}