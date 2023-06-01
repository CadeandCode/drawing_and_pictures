package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

// Creates a class view for the context and attribute set
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs){

    // Variable of CustomPath inner class to use it further
    private var mDrawPath : CustomPath? = null
    // Instance of the Bitmap
    private var mCanvasBitmap: Bitmap? = null
    // Paint class holds the style and color info about how to draw
    private var mDrawPaint: Paint? = null
    // Instance of canvas paint view
    private var mCanvasPaint: Paint? = null
    // Variable for stroke/brush size to draw on canvas
    private var mBrushSize: Float = 0.toFloat()
    // Variable to hold a color of the stroke
    private var color = Color.BLACK
    // Variable of the blank Canvas to hold input colors
    private var canvas: Canvas? = null
    // Creates an array list for custom paths
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init{
        setUpDrawing()
    }

    // Retrieves item removed through undo paths
    // Allows the undo button to function
    fun onClickUndo(){
        if(mPaths.size > 0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size -1))
            invalidate()
        }
    }

    // The two variables are not null so they can be used
    // Sets the style to stroke and then adjusts it
    // Sets up a dither and the size of brush
    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()
    }

    // Overrides the function to adjust the canvas bitmap and include an array of colors
    // Canvas is set to the canvas bitmap and the !! label it as null
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    // Draws the specific bitmap with original width and height
    // Canvas has "?" removed so it is no longer null
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        // Keeps the users input/drawing on the screen
        for (path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        // Denotes what should happen if user starts drawing
        if (!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    // Allows something to happen when the interface is touched/fills the mDrawPath
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        // Denotes what should happen exactly when the user touches the screen
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                // Forces a reset if the touches are null
                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY !=null){
                        mDrawPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    // Sets the brush size dynamically for different screens
    fun setSizeForBrush(newSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        newSize, resources.displayMetrics)

        mDrawPaint!!.strokeWidth = mBrushSize
    }

    // Parses a string into a color, making the draw paint color the color that is chosen
    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    // Inner class for custom path with two params
    // CustomPath is only usable in Drawing View
    // Path imported through android library and is used for drawing
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path(){

    }
}