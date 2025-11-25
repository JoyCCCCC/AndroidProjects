package com.example.gyroball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onResume() {
        super.onResume()
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_GYROSCOPE) {
                val x = it.values[0]
                val y = it.values[1]

                gameView.updateBallPosition(y, x)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    inner class GameView(context: Context) : View(context) {
        private val ballPaint = Paint().apply {
            color = Color.RED
            isAntiAlias = true
        }

        private val wallPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        private val obstaclePaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }

        private val goalPaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        private val textPaint = Paint().apply {
            color = Color.RED
            textSize = 60f
            isAntiAlias = true
            isFakeBoldText = true
        }

        private var ballX = 0f
        private var ballY = 0f
        private val ballRadius = 22f
        private var ballVelocityX = 0f
        private var ballVelocityY = 0f

        private var initialBallX = 80f
        private var initialBallY = 80f

        private val sensitivity = 20f
        private val friction = 1.0f
        private val maxVelocity = 50f

        private var screenWidth = 0
        private var screenHeight = 0

        private val walls = mutableListOf<RectF>()
        private val obstacles = mutableListOf<RectF>()
        private var goal: RectF? = null

        private var gameWon = false

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)

            screenWidth = w
            screenHeight = h

            initialBallX = 80f
            initialBallY = 80f
            resetBall()

            val wallThickness = 30f
            walls.apply {
                clear()
                add(RectF(0f, 0f, w.toFloat(), wallThickness))
                add(RectF(0f, h - wallThickness, w.toFloat(), h.toFloat()))
                add(RectF(0f, 0f, wallThickness, h.toFloat()))
                add(RectF(w - wallThickness, 0f, w.toFloat(), h.toFloat()))
            }

            val wf = w.toFloat()
            val hf = h.toFloat()

            obstacles.apply {
                clear()

                add(RectF(wf * 0.25f, hf * 0.15f, wf * 0.30f, hf * 0.55f))

                add(RectF(wf * 0.35f, hf * 0.08f, wf * 0.70f, hf * 0.13f))

                add(RectF(wf * 0.55f, hf * 0.20f, wf * 0.60f, hf * 0.50f))

                add(RectF(wf * 0.10f, hf * 0.60f, wf * 0.45f, hf * 0.65f))

                add(RectF(wf * 0.40f, hf * 0.55f, wf * 0.45f, hf * 0.85f))

                add(RectF(wf * 0.65f, hf * 0.40f, wf * 0.90f, hf * 0.45f))

                add(RectF(wf * 0.20f, hf * 0.75f, wf * 0.65f, hf * 0.80f))

                add(RectF(wf * 0.70f, hf * 0.70f, wf * 0.75f, hf * 0.85f))
            }

            val goalSize = min(wf, hf) * 0.15f
            goal = RectF(
                wf - goalSize - 50f,
                hf - goalSize - 50f,
                wf - 50f,
                hf - 50f
            )
        }

        private fun resetBall() {
            ballX = initialBallX
            ballY = initialBallY
            ballVelocityX = 0f
            ballVelocityY = 0f
            ballPaint.color = Color.RED
            gameWon = false
        }

        fun updateBallPosition(gyroX: Float, gyroY: Float) {
            if (gameWon) {
                return
            }

            ballVelocityX += gyroX * sensitivity
            ballVelocityY += gyroY * sensitivity

            ballVelocityX = ballVelocityX.coerceIn(-maxVelocity, maxVelocity)
            ballVelocityY = ballVelocityY.coerceIn(-maxVelocity, maxVelocity)

            ballVelocityX *= friction
            ballVelocityY *= friction

            val newX = ballX + ballVelocityX
            val newY = ballY + ballVelocityY

            if (!checkCollision(newX, newY)) {
                ballX = newX
                ballY = newY
            } else {
                ballVelocityX *= -0.5f
                ballVelocityY *= -0.5f
            }

            constrainToBounds()

            goal?.let {
                if (it.contains(ballX, ballY)) {
                    ballPaint.color = Color.YELLOW
                    gameWon = true
                }
            }

            invalidate()
        }

        private fun constrainToBounds() {
            val margin = ballRadius + 35f
            if (ballX < margin) {
                ballX = margin
                ballVelocityX = 0f
            }
            if (ballX > screenWidth - margin) {
                ballX = screenWidth - margin
                ballVelocityX = 0f
            }
            if (ballY < margin) {
                ballY = margin
                ballVelocityY = 0f
            }
            if (ballY > screenHeight - margin) {
                ballY = screenHeight - margin
                ballVelocityY = 0f
            }
        }

        private fun checkCollision(x: Float, y: Float): Boolean {
            for (wall in walls) {
                if (circleIntersectsRect(x, y, ballRadius, wall)) {
                    return true
                }
            }

            for (obstacle in obstacles) {
                if (circleIntersectsRect(x, y, ballRadius, obstacle)) {
                    return true
                }
            }

            return false
        }

        private fun circleIntersectsRect(cx: Float, cy: Float, radius: Float, rect: RectF): Boolean {
            val closestX = max(rect.left, min(cx, rect.right))
            val closestY = max(rect.top, min(cy, rect.bottom))

            val distanceX = cx - closestX
            val distanceY = cy - closestY
            val distanceSquared = distanceX * distanceX + distanceY * distanceY

            return distanceSquared < (radius * radius)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            canvas.drawColor(Color.WHITE)

            for (wall in walls) {
                canvas.drawRect(wall, wallPaint)
            }

            for (obstacle in obstacles) {
                canvas.drawRect(obstacle, obstaclePaint)
            }

            goal?.let {
                canvas.drawRect(it, goalPaint)
            }

            canvas.drawCircle(ballX, ballY, ballRadius, ballPaint)

            if (gameWon) {
                canvas.drawText("🎉 You Win! 🎉", screenWidth * 0.15f, screenHeight * 0.1f, textPaint)
            }
        }
    }
}