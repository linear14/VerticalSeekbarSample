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
import kotlin.math.min

class BitdamSeekBar: View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private var isInit = true

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

    private val minValue: Int

    var maxValue: Int
        set(value) {
            field = value
            invalidate()
        }

    private var valueBoundaryList: MutableList<Float>? = null

    private var defaultProgress: Int = 0
    var progress: Int? = null

    private var thumbRect: Rect? = null
    private var thumbAvailable = false

    private var onProgressChangeListener: ((Int) -> Unit)? = null
    private var onPressListener: (() -> Unit)? = null
    private var onReleaseListener: (() -> Unit)? = null

    init {
        thumbRadius = 20.dpToPx()
        verticalOffset = 25.dpToPx()
        minValue = 0
        maxValue = 200
        thumbAvailable = false
        isInit = true
        // Log.d("SEEKBAR_TOUCH", "thumbRect: 좌우 __ ${left} ~ ${right}, 상하 __ ${top} ~ ${bottom}")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
       /* val params = layoutParams as ConstraintLayout.LayoutParams
        Log.d("SEEKBAR_LOGGER", "params height: ${params.height}")*/

        drawBar(canvas)
        drawThumb(canvas)

        activateThumbTouchListener()

        if(isInit) {
            setThumbLevel(progress?:defaultProgress)
            isInit = false
        }
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

    fun setOnPressListener(listener: (() -> Unit)?) {
        this.onPressListener = listener
    }

    fun setOnReleaseListener(listener: (() -> Unit)?) {
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
                            thumbAvailable = true
                        }
                    }

                }

                // 3.coerceIn(3..4)
                MotionEvent.ACTION_MOVE -> {
                    if(valueBoundaryList == null) {
                        makeBoundaryList()
                    }

                    // Log.d("SEEKBAR_TOUCH", "RECT: 좌우 __ ${thumbRect?.left} ~ ${thumbRect?.right}, 상하 __ ${thumbRect?.top} ~ ${thumbRect?.bottom}")
                    if(thumbAvailable) {
                        thumbRect?.let { thumbRect ->
                            var (x, y) = event.x to event.y + top

                            // Log.d("SEEKBAR_TOUCH", "뷰의 상하 __ ${top} ~ ${bottom}")
                            // Log.d("SEEKBAR_TOUCH", "실제 클릭: x: ${event.x}, y: ${event.y}")

                            if(y <= top + verticalOffset) {
                                y = top + verticalOffset
                                onProgressChangeListener?.invoke(maxValue)
                            } else if(y >= bottom - verticalOffset) {
                                y = bottom - verticalOffset
                                onProgressChangeListener?.invoke(minValue)
                            } else {
                                valueBoundaryList?.let { valueBoundaryList ->
                                    for(i in minValue .. maxValue) {
                                        if(i != maxValue) {
                                            if(y >= valueBoundaryList[i] && y < valueBoundaryList[i + 1]) {
                                                y = valueBoundaryList[i]
                                                onProgressChangeListener?.invoke(maxValue - i)
                                                break
                                            }
                                        } else {
                                            y = bottom - verticalOffset
                                            onProgressChangeListener?.invoke(minValue)
                                        }
                                    }
                                }

                            }

                            this.thumbRect = Rect(
                                width / 2 - thumbRadius.toInt(),
                                (y - top - thumbRadius).toInt(),
                                width / 2 + thumbRadius.toInt(),
                                (y - top + thumbRadius).toInt()
                            )

                            invalidate()
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    thumbAvailable = false
                    onReleaseListener?.invoke()
                    view.performClick()
                }
            }
            true
        }
    }

    fun setThumbLevel(level: Int) {
        if(valueBoundaryList == null) {
            makeBoundaryList()
        }

        val realLevel = maxValue - level

        valueBoundaryList?.let { valueBoundaryList ->
            thumbRect = Rect(
                width / 2 - thumbRadius.toInt(),
                (valueBoundaryList[realLevel] - top - thumbRadius).toInt(),
                width / 2 + thumbRadius.toInt(),
                (valueBoundaryList[realLevel] - top + thumbRadius).toInt()
            )
        }
        onProgressChangeListener?.invoke(level)
        invalidate()
    }

    private fun makeBoundaryList() {
        valueBoundaryList = mutableListOf()

        val start = top + verticalOffset
        val end = bottom - verticalOffset
        val interval = (end - start) / maxValue

        for(i in minValue .. maxValue) {
            valueBoundaryList?.add(start + interval * i)
        }

        valueBoundaryList?.let { list ->
            if(list[maxValue] != end) {
                list[maxValue] = end
            }
        }
    }


}