package io.github.nahkd123.tinycanvas.engine.layer

class GroupLayer : Layer {
    val children = mutableListOf<Layer.Info>()

    override fun get(x: Int, y: Int): Int {
        var base = 0
        for ((layer, _, blendMode) in children) base = blendMode.blend(layer[x, y], base)
        return base
    }
}