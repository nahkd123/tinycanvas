package io.github.nahkd123.tinycanvas.engine.layer

import androidx.collection.mutableLongObjectMapOf

class RasterLayer : Layer {
    private val tiles = mutableLongObjectMapOf<IntArray>()

    override fun get(x: Int, y: Int): Int = tiles[tileId(x, y)]
        ?.let { tile -> tile[x % TILE_SIZE + (y % TILE_SIZE) * TILE_SIZE] }
        ?: 0

    operator fun set(x: Int, y: Int, value: Int) = tiles
        .getOrPut(tileId(x, y)) { IntArray(TILE_SIZE * TILE_SIZE) }
        .let { tile -> tile[x % TILE_SIZE + (y % TILE_SIZE) * TILE_SIZE] = value }
}

private const val TILE_SIZE = 64
private val Long.tileFrame: Int inline get() = (this and 0xFFFFFFFFL).toInt()
private val Long.tileX: Int inline get() = ((this shr 32) and 0xFFFFL).toInt()
private val Long.tileY: Int inline get() = ((this shr 48) and 0xFFFFL).toInt()
private fun tileId(x: Int, y: Int, frame: Int = 0): Long =
    ((y and 0xFFFF) shl 48).toLong() or
    ((x and 0xFFFF) shl 32).toLong() or
    frame.toLong()