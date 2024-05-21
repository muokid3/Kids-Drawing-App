package com.dm.berxley.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attributes: AttributeSet): View(context, attributes) {
    private var mDrawingPath: CustomPath ? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint?  = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawingPath = CustomPath(color,mBrushSize)
        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE
        mDrawPaint?.strokeJoin = Paint.Join.ROUND
        mDrawPaint?.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mCanvasBitmap?.let {
            canvas.drawBitmap(it,0f,0f, mCanvasPaint)
        }

        for (path in mPaths){
            mDrawPaint?.strokeWidth = path.brushThickness
            mDrawPaint?.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if (!mDrawingPath!!.isEmpty){
            mDrawPaint?.strokeWidth = mDrawingPath!!.brushThickness
            mDrawPaint?.color = mDrawingPath!!.color
            canvas.drawPath(mDrawingPath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawingPath?.color = color
                mDrawingPath?.brushThickness = mBrushSize
                mDrawingPath?.reset()
                mDrawingPath?.moveTo(
                    touchX,
                    touchY
                )
            }

            MotionEvent.ACTION_MOVE -> {
                mDrawingPath?.lineTo(
                    touchX,
                    touchY
                )
            }

            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawingPath!!)
                mDrawingPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true
    }
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()
}