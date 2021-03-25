package com.ldh.verticalseekbarsample

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat

class BitdamSeekBar: View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        flags = Paint.ANTI_ALIAS_FLAG
    }

    var thumbRadius: Float
        set(value) {
            field = value
            invalidate()
        }

    var verticalOffset: Float
        set(value) {
            field = value
            invalidate()
        }

    private var thumbRect: Rect? = null

    private var onProgressChangeListener: ((Int) -> Unit)? = null
    private var onPressListener: ((Int) -> Unit)? = null
    private var onReleaseListener: ((Int) -> Unit)? = null

    init {
        thumbRadius = 20.dpToPx()
        verticalOffset = 25.dpToPx()

        // Log.d("SEEKBAR_TOUCH", "thumbRect: 좌우 __ ${left} ~ ${right}, 상하 __ ${top} ~ ${bottom}")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
       /* val params = layoutParams as ConstraintLayout.LayoutParams
        Log.d("SEEKBAR_LOGGER", "params height: ${params.height}")*/

        drawBar(canvas)
        drawThumb(canvas)

        activateThumbTouchListener()
    }

    private fun drawBar(canvas: Canvas) {
        /*Log.d("SEEKBAR_LOGGER", "height: $height")
        Log.d("SEEKBAR_LOGGER", "dpToPx: ${300.dpToPx()}")*/
        canvas.drawRect(
            width / 2 - 1f,
            verticalOffset,
            width / 2 + 1f,
            (height - verticalOffset),
            paint
        )
    }

    private fun drawThumb(canvas: Canvas) {
        if(thumbRect == null) {
            thumbRect = Rect(
                width / 2 - thumbRadius.toInt(),
                height - verticalOffset.toInt() - thumbRadius.toInt(),
                width / 2 + thumbRadius.toInt(),
                height - verticalOffset.toInt() + thumbRadius.toInt()
            )
        }

        thumbRect?.let {
            canvas.drawCircle((it.left + it.right) / 2f, (it.top + it.bottom) / 2f, thumbRadius, paint)
        }

    }

    fun Int.dpToPx(): Float {
        return (this * Resources.getSystem().displayMetrics.density)
    }

    fun setOnProgressChangeListener(listener: ((Int) -> Unit)?) {
        this.onProgressChangeListener = listener
    }

    fun setOnPressListener(listener: ((Int) -> Unit)?) {
        this.onPressListener = listener
    }

    fun setOnReleaseListener(listener: ((Int) -> Unit)?) {
        this.onReleaseListener = listener
    }

    private fun activateThumbTouchListener() {
        setOnTouchListener { view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Log.d("SEEKBAR_TOUCH", "RECT: 좌우 __ ${thumbRect?.left} ~ ${thumbRect?.right}, 상하 __ ${thumbRect?.top} ~ ${thumbRect?.bottom}")
                    // Log.d("SEEKBAR_TOUCH", "실제 클릭: x: ${event.x}, y: ${event.y}")

                    thumbRect?.let { thumbRect ->
                        if(thumbRect.contains(event.x.toInt(), event.y.toInt())) {
                            Toast.makeText(context, "ACTION_DOWN", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // 3.coerceIn(3..4)
                MotionEvent.ACTION_MOVE -> {
                    // Log.d("SEEKBAR_TOUCH", "RECT: 좌우 __ ${thumbRect?.left} ~ ${thumbRect?.right}, 상하 __ ${thumbRect?.top} ~ ${thumbRect?.bottom}")
                    thumbRect?.let { thumbRect ->
                        val (x, y) = event.x to event.y + top

                        Log.d("SEEKBAR_TOUCH", "뷰의 상하 __ ${top} ~ ${bottom}")
                        Log.d("SEEKBAR_TOUCH", "실제 클릭: x: ${event.x}, y: ${event.y}")

                        when {
                            y < top + verticalOffset -> {
                                this.thumbRect = Rect(
                                    width / 2 - thumbRadius.toInt(),
                                    (top + verticalOffset - thumbRadius).toInt(),
                                    width / 2 + thumbRadius.toInt(),
                                    (top + verticalOffset + thumbRadius).toInt()
                                )
                            }

                            y in top + verticalOffset .. bottom - verticalOffset-> {
                                this.thumbRect = Rect(
                                    width / 2 - thumbRadius.toInt(),
                                    (event.y - thumbRadius).toInt(),
                                    width / 2 + thumbRadius.toInt(),
                                    (event.y + thumbRadius).toInt()
                                )
                            }

                            else -> {
                                this.thumbRect = Rect(
                                    width / 2 - thumbRadius.toInt(),
                                    (bottom - verticalOffset - thumbRadius).toInt(),
                                    width / 2 + thumbRadius.toInt(),
                                    (bottom - verticalOffset + thumbRadius).toInt()
                                )
                            }
                        }

                        invalidate()
                    }
                }

                MotionEvent.ACTION_UP -> {
                    view.performClick()
                }
            }
            true
        }
    }
}