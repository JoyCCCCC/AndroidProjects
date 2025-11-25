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

        // 初始化传感器
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // 创建游戏视图
        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onResume() {
        super.onResume()
        // 注册陀螺仪传感器监听器
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        // 取消注册传感器监听器
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_GYROSCOPE) {
                // 获取陀螺仪数据（角速度）
                val x = it.values[0] // 绕X轴旋转（前后倾斜）
                val y = it.values[1] // 绕Y轴旋转（左右倾斜）

                // 更新小球位置
                gameView.updateBallPosition(y, x)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 不需要处理精度变化
    }

    // 自定义游戏视图类
    inner class GameView(context: Context) : View(context) {

        // 画笔
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

        // 小球属性
        private var ballX = 0f
        private var ballY = 0f
        private val ballRadius = 22f
        private var ballVelocityX = 0f
        private var ballVelocityY = 0f

        // 初始位置（用于重置）
        private var initialBallX = 80f
        private var initialBallY = 80f

        // 游戏参数
        private val sensitivity = 20f // 灵敏度
        private val friction = 1.0f // 摩擦系数
        private val maxVelocity = 50f // 最大速度限制

        // 屏幕尺寸
        private var screenWidth = 0
        private var screenHeight = 0

        // 墙壁和障碍物
        private val walls = mutableListOf<RectF>()
        private val obstacles = mutableListOf<RectF>()
        private var goal: RectF? = null

        // 游戏状态
        private var gameWon = false

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)

            screenWidth = w
            screenHeight = h

            // 设置小球初始位置（左上角）
            initialBallX = 80f
            initialBallY = 80f
            resetBall()

            // 创建外墙（边界）- 更厚一些
            val wallThickness = 30f
            walls.apply {
                clear()
                add(RectF(0f, 0f, w.toFloat(), wallThickness)) // 顶部墙
                add(RectF(0f, h - wallThickness, w.toFloat(), h.toFloat())) // 底部墙
                add(RectF(0f, 0f, wallThickness, h.toFloat())) // 左侧墙
                add(RectF(w - wallThickness, 0f, w.toFloat(), h.toFloat())) // 右侧墙
            }

            // 创建全屏迷宫 - 使用屏幕尺寸的百分比来创建障碍物
            val wf = w.toFloat()
            val hf = h.toFloat()

            obstacles.apply {
                clear()

                // 第一层 - 左侧长条（从上到中）
                add(RectF(wf * 0.25f, hf * 0.15f, wf * 0.30f, hf * 0.55f))

                // 第二层 - 中间上方长条（水平）
                add(RectF(wf * 0.35f, hf * 0.08f, wf * 0.70f, hf * 0.13f))

                // 第三层 - 中间偏右长条（垂直）
                add(RectF(wf * 0.55f, hf * 0.20f, wf * 0.60f, hf * 0.50f))

                // 第四层 - 下方左侧长条（水平）
                add(RectF(wf * 0.10f, hf * 0.60f, wf * 0.45f, hf * 0.65f))

                // 第五层 - 中间下方长条（垂直）
                add(RectF(wf * 0.40f, hf * 0.55f, wf * 0.45f, hf * 0.85f))

                // 第六层 - 右侧中间长条（水平）
                add(RectF(wf * 0.65f, hf * 0.40f, wf * 0.90f, hf * 0.45f))

                // 第七层 - 底部中间长条（水平）
                add(RectF(wf * 0.20f, hf * 0.75f, wf * 0.65f, hf * 0.80f))

                // 第八层 - 右下角障碍（不要挡住目标）
                add(RectF(wf * 0.70f, hf * 0.70f, wf * 0.75f, hf * 0.85f))
            }

            // 创建目标区域（右下角）- 更大更明显
            val goalSize = min(wf, hf) * 0.15f // 目标区域大小为屏幕较小边的15%
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
            // 如果游戏已结束，不更新位置
            if (gameWon) {
                return
            }

            // 根据陀螺仪数据更新速度
            ballVelocityX += gyroX * sensitivity
            ballVelocityY += gyroY * sensitivity

            // 限制最大速度（防止穿透墙壁）
            ballVelocityX = ballVelocityX.coerceIn(-maxVelocity, maxVelocity)
            ballVelocityY = ballVelocityY.coerceIn(-maxVelocity, maxVelocity)

            // 应用摩擦力
            ballVelocityX *= friction
            ballVelocityY *= friction

            // 计算新位置
            val newX = ballX + ballVelocityX
            val newY = ballY + ballVelocityY

            // 检查碰撞
            if (!checkCollision(newX, newY)) {
                ballX = newX
                ballY = newY
            } else {
                // 碰撞时反弹
                ballVelocityX *= -0.5f
                ballVelocityY *= -0.5f
            }

            // 强制边界约束（双重保护）
            constrainToBounds()

            // 检查是否到达目标
            goal?.let {
                if (it.contains(ballX, ballY)) {
                    // 到达目标，小球变黄色
                    ballPaint.color = Color.YELLOW
                    gameWon = true
                }
            }

            // 重绘视图
            invalidate()
        }

        private fun constrainToBounds() {
            // 强制限制小球在屏幕内
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
            // 检查与墙壁的碰撞
            for (wall in walls) {
                if (circleIntersectsRect(x, y, ballRadius, wall)) {
                    return true
                }
            }

            // 检查与障碍物的碰撞
            for (obstacle in obstacles) {
                if (circleIntersectsRect(x, y, ballRadius, obstacle)) {
                    return true
                }
            }

            return false
        }

        private fun circleIntersectsRect(cx: Float, cy: Float, radius: Float, rect: RectF): Boolean {
            // 找到矩形上最接近圆心的点
            val closestX = max(rect.left, min(cx, rect.right))
            val closestY = max(rect.top, min(cy, rect.bottom))

            // 计算圆心到最近点的距离
            val distanceX = cx - closestX
            val distanceY = cy - closestY
            val distanceSquared = distanceX * distanceX + distanceY * distanceY

            // 如果距离小于半径，则相交
            return distanceSquared < (radius * radius)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // 绘制背景
            canvas.drawColor(Color.WHITE)

            // 绘制墙壁
            for (wall in walls) {
                canvas.drawRect(wall, wallPaint)
            }

            // 绘制障碍物
            for (obstacle in obstacles) {
                canvas.drawRect(obstacle, obstaclePaint)
            }

            // 绘制目标区域
            goal?.let {
                canvas.drawRect(it, goalPaint)
            }

            // 绘制小球
            canvas.drawCircle(ballX, ballY, ballRadius, ballPaint)

            // 显示游戏状态
            if (gameWon) {
                canvas.drawText("🎉 You Win! 🎉", screenWidth * 0.15f, screenHeight * 0.1f, textPaint)
            }
        }
    }
}