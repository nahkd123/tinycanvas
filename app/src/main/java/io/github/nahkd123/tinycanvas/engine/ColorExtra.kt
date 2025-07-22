package io.github.nahkd123.tinycanvas.engine

import android.graphics.Color
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

val Int.alphaF: Float inline get() = this.alpha / 255f
val Int.redF: Float inline get() = this.red / 255f
val Int.greenF: Float inline get() = this.green / 255f
val Int.blueF: Float inline get() = this.blue / 255f

fun colorArgb8888(a: Float, r: Float, g: Float, b: Float): Int = Color.argb(a, r, g, b)