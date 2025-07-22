package io.github.nahkd123.tinycanvas.android.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.graphics.lowlatency.BufferInfo
import androidx.graphics.lowlatency.GLFrontBufferedRenderer
import androidx.graphics.opengl.egl.EGLManager
import io.github.nahkd123.tinycanvas.engine.layer.Layer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

@SuppressLint("ClickableViewAccessibility")
class TinyCanvasView : SurfaceView {
    val layer: Layer

    constructor(context: Context, layer: Layer) : super(context) {
        this.layer = layer
    }

    private data class Line(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    ) {
        val distance get() = sqrt((x2 - x1).pow(2f) + (y2 - y1).pow(2f))

        infix fun writeTo(buffer: ByteBuffer) {
            buffer.putFloat(x1)
            buffer.putFloat(y1)
            buffer.putFloat(x2)
            buffer.putFloat(y2)
        }
    }

    private val glRenderer: GLFrontBufferedRenderer<Line> = GLFrontBufferedRenderer(this, object : GLFrontBufferedRenderer.Callback<Line> {
        var initialized = false
        var vertexShader: Int = -1
        var fragmentShader: Int = -1
        var program: Int = -1

        var uModelViewProjection: Int = -1
        var uColor: Int = -1
        var aPosition: Int = -1

        fun compileShader(type: Int, source: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)

            if (IntArray(1).let { GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, it, 0); it[0] } == GLES20.GL_FALSE) {
                val log = GLES20.glGetShaderInfoLog(shader)
                GLES20.glDeleteShader(shader)
                throw RuntimeException("Shader compile error: $log")
            }

            return shader
        }

        fun initialize() {
            if (initialized) return
            initialized = true

            vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, """
                uniform mat4 uModelViewProjection;
                attribute vec2 aPosition;
                
                void main() {
                    gl_Position = uModelViewProjection * vec4(aPosition, 0.0, 1.0);
                }
            """.trimIndent())

            fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, """
                precision mediump float;
                
                uniform vec4 uColor;
                
                void main() {
                    gl_FragColor = uColor;
                }
            """.trimIndent())

            program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)

            if (IntArray(1).let { GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, it, 0); it[0] } == GLES20.GL_FALSE) {
                val log = GLES20.glGetProgramInfoLog(program)
                GLES20.glDeleteShader(vertexShader)
                GLES20.glDeleteShader(fragmentShader)
                GLES20.glDeleteProgram(program)
                throw RuntimeException("Program link error: $log")
            }

            uModelViewProjection = GLES20.glGetUniformLocation(program, "uModelViewProjection")
            uColor = GLES20.glGetUniformLocation(program, "uColor")
            aPosition = GLES20.glGetAttribLocation(program, "aPosition")
        }

        fun drawLine(lines: Iterable<Line>, mvp: FloatArray) {
            GLES20.glUseProgram(program)
            GLES20.glLineWidth(2f)
            GLES20.glEnableVertexAttribArray(aPosition)

            val buf = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder())
            GLES20.glUniformMatrix4fv(uModelViewProjection, 1, false, mvp, 0)
            GLES20.glUniform4f(uColor, Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)

            for (line in lines) {
                line writeTo buf
                buf.position(0)
                GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 8, buf)
                GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
            }

            GLES20.glDisableVertexAttribArray(aPosition)
        }

        override fun onDrawFrontBufferedLayer(
            eglManager: EGLManager,
            width: Int,
            height: Int,
            bufferInfo: BufferInfo,
            transform: FloatArray,
            param: Line
        ) {
            initialize()
            val bufferWidth = bufferInfo.width
            val bufferHeight = bufferInfo.height
            GLES20.glViewport(0, 0, bufferWidth, bufferHeight)

            val mat = FloatArray(16).also { Matrix.orthoM(it, 0, 0f, bufferWidth.toFloat(), 0f, bufferHeight.toFloat(), -1f, 1f) }
            val mvp = FloatArray(16).also { Matrix.multiplyMM(it, 0, mat, 0, transform, 0) }

            drawLine(listOf(param), mvp)
        }

        override fun onDrawMultiBufferedLayer(
            eglManager: EGLManager,
            width: Int,
            height: Int,
            bufferInfo: BufferInfo,
            transform: FloatArray,
            params: Collection<Line>
        ) {
            initialize()
            val bufferWidth = bufferInfo.width
            val bufferHeight = bufferInfo.height
            GLES20.glViewport(0, 0, bufferWidth, bufferHeight)

            val mat = FloatArray(16).also { Matrix.orthoM(it, 0, 0f, bufferWidth.toFloat(), 0f, bufferHeight.toFloat(), -1f, 1f) }
            val mvp = FloatArray(16).also { Matrix.multiplyMM(it, 0, mat, 0, transform, 0) }

            drawLine(params, mvp)
        }
    })

    init {
        var lastX = 0f
        var lastY = 0f
        var counter = 0f

        setOnTouchListener { view, event ->
            if (event == null) false

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    requestUnbufferedDispatch(event)
                    lastX = event.x
                    lastY = event.y
                    counter = 0f
                }
                MotionEvent.ACTION_MOVE -> {
                    val line = Line(lastX, lastY, event.x, event.y)
                    lastX = event.x
                    lastY = event.y
                    counter += line.distance
                    glRenderer.renderFrontBufferedLayer(line)

                    if (counter > 1000f) {
                        glRenderer.commit()
                        counter = 0f
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    glRenderer.cancel()
                }
                MotionEvent.ACTION_UP -> {
                    glRenderer.commit()
                }
            }

            true
        }
    }
}