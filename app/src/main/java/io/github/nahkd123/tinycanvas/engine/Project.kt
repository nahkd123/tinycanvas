package io.github.nahkd123.tinycanvas.engine

/**
 * Represent a project that is currently being opened (a.k.a loaded in memory). To keep things
 * simple, all implementations targeting this interface are hardcoded to use ARGB8888 pixel format
 * and sRGB color profile. The underlying project format that is saved on device's storage, however,
 * is flexible enough to support different pixel formats and color profiles.
 */
interface Project {
    /**
     * The width of project's canvas, measured in pixels.
     */
    val width: Int

    /**
     * The height of project's canvas, measured in pixels.
     */
    val height: Int
}