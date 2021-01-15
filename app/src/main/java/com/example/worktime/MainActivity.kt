package com.example.worktime

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.worktime.databinding.ActivityMainBinding
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    companion object {
        private lateinit var binding: ActivityMainBinding
        private lateinit var toolbar: Toolbar
        private var bottomFragment: TimerFragment? = null
        private lateinit var onSwipeTouchListener: OnSwipeTouchListener
        
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Define bottom dialog fragment, bottom swipe
        bottomFragment = TimerFragment()
        onSwipeTouchListener = OnSwipeTouchListener(this, binding.root)

        //Toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_timer) {
            Toast.makeText(this, "Menu timer is clicked", Toast.LENGTH_SHORT).show()
            bottomFragment?.show(supportFragmentManager, bottomFragment?.tag)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    inner class OnSwipeTouchListener internal constructor(ctx: Context, mainView: View) :
        OnTouchListener {
        private val gestureDetector: GestureDetector
        private var context: Context

        init {
            gestureDetector = GestureDetector(ctx, GestureListener())
            mainView.setOnTouchListener(this)
            context = ctx
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return gestureDetector.onTouchEvent(event)
        }

        inner class GestureListener : SimpleOnGestureListener() {

            private var screenWidth = 650   //Default value
            private var screenHeight = 1050 //Default value
            private val swipeDistance = 150 //Default value

            //private val swipeVelocity = 100
            init {
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getRealMetrics(metrics)
                screenWidth = metrics.widthPixels
                screenHeight = metrics.heightPixels
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onFling(
                e1: MotionEvent, e2: MotionEvent,
                velocityX: Float, velocityY: Float
            ): Boolean {
                var result = false
                val deltaY = e2.y - e1.y
                val deltaX = e2.x - e1.x

                try {
                    if (abs(deltaX) > abs(deltaY)) {
                        if (abs(deltaX) > swipeDistance) {
                            if (deltaX > 0 && e1.x < 70) onSwipeRight() else if (deltaX < 0 && e1.x > screenWidth - 70) onSwipeLeft()
                            result = true
                        }
                    } else if (abs(deltaY) > swipeDistance) {
                        if (deltaY > 0 && e1.y < 200 && e1.y > 100) onSwipeBottom() else if (deltaY < 0 && e1.y > screenHeight - 200) onSwipeTop()
                        result = true
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                return result
            }

        }

        fun onSwipeRight() {
            val text = "Swiped Right"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, text, duration)
            toast.show()
            toast.setGravity(Gravity.CENTER_VERTICAL or Gravity.START, 0, 0)
        }

        fun onSwipeLeft() {
            val text = "Swiped Left"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, text, duration)
            toast.show()
            toast.setGravity(Gravity.CENTER_VERTICAL or Gravity.END, 0, 0)
        }

        fun onSwipeTop() {
            Toast.makeText(context, "Swiped Up", Toast.LENGTH_SHORT).show()
            bottomFragment?.show(supportFragmentManager, bottomFragment?.tag)
        }

        fun onSwipeBottom() {
            val text = "Swiped Down"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, text, duration)
            toast.show()
            toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, 0)
        }

    }
}