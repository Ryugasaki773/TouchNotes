package com.ryugasaki773.touchnotes

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        setContentView(HandwritingView(this))
    }
}

class HandwritingView(context: Context) : View(context) {

    private val paths = mutableListOf<Path>()
    private var currentPath: Path? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    init {
        setBackgroundColor(Color.WHITE)
        isFocusable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (path in paths) {
            canvas.drawPath(path, paint)
        }

        currentPath?.let {
            canvas.drawPath(it, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                currentPath = Path().apply { moveTo(x, y) }
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(x, y)
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                currentPath?.let {
                    it.lineTo(x, y)
                    paths.add(it)
                }
                currentPath = null
                parent?.requestDisallowInterceptTouchEvent(false)
                invalidate()
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                currentPath = null
                parent?.requestDisallowInterceptTouchEvent(false)
                invalidate()
                return true
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
