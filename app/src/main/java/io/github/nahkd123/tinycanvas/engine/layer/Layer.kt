package io.github.nahkd123.tinycanvas.engine.layer

interface Layer {
    /**
     * Sample a pixel at given coordinates. The coordinates can be outside of project's bounds, but
     * the sampled pixel will be transparent/completely zero. The pixel format for returned value
     * is ARGB8888: 8 most significant bits are for alpha channel, the next 24 bits are for red,
     * green and blue channels respectively.
     */
    operator fun get(x: Int, y: Int): Int

    data class Info(val layer: Layer, var name: String, var blendMode: BlendMode) {}
}