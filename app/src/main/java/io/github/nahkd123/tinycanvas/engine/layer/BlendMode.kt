package io.github.nahkd123.tinycanvas.engine.layer

import io.github.nahkd123.tinycanvas.engine.alphaF
import io.github.nahkd123.tinycanvas.engine.blueF
import io.github.nahkd123.tinycanvas.engine.colorArgb8888
import io.github.nahkd123.tinycanvas.engine.greenF
import io.github.nahkd123.tinycanvas.engine.redF

enum class BlendMode {
    /**
     * Source over destination blending mode, also known as "Normal". This is the default blending
     * mode for all layers.
     */
    SourceOver {
        override fun blend(src: Int, dst: Int): Int {
            val finalAlpha = src.alphaF + dst.alphaF * (1f - src.alphaF)
            return colorArgb8888(
                a = finalAlpha,
                r = (src.redF * src.alphaF + dst.redF * dst.alphaF * (1f - src.alphaF)) / finalAlpha,
                g = (src.greenF * src.alphaF + dst.greenF * dst.alphaF * (1f - src.alphaF)) / finalAlpha,
                b = (src.blueF * src.alphaF + dst.blueF * dst.alphaF * (1f - src.alphaF)) / finalAlpha
            )
        }
    };

    /**
     * Blend pixels.
     *
     * @param src Color data from source, which is usually the new data to blend on top of existing
     * one.
     * @param dst Color data from destination, which is usually the existing data.
     */
    abstract fun blend(src: Int, dst: Int): Int
}