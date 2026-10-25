package com.agrovision.ai.camera

import android.graphics.Bitmap
import android.graphics.Color
import com.agrovision.ai.core.common.Constants

class ImageQualityGuard {

    fun checkQuality(bitmap: Bitmap): QualityCheckResult {
        val brightness = computeAverageBrightness(bitmap)
        if (brightness < Constants.MIN_BRIGHTNESS) {
            return QualityCheckResult.TooDark(brightness)
        }
        if (brightness > Constants.MAX_BRIGHTNESS) {
            return QualityCheckResult.TooBright(brightness)
        }

        val laplacianVariance = computeLaplacianVariance(bitmap)
        if (laplacianVariance < Constants.MIN_LAPLACIAN_VARIANCE) {
            return QualityCheckResult.Blurry(laplacianVariance)
        }

        return QualityCheckResult.Passed
    }

    fun computeAverageBrightness(bitmap: Bitmap): Double {
        val width = bitmap.width
        val height = bitmap.height
        val stepX = (width / 40).coerceAtLeast(1)
        val stepY = (height / 40).coerceAtLeast(1)

        var totalLuminance = 0.0
        var count = 0

        var y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                // Standard ITU-R BT.601 perceptual luminance formula
                val luminance = 0.299 * r + 0.587 * g + 0.114 * b
                totalLuminance += luminance
                count++
                x += stepX
            }
            y += stepY
        }

        return if (count > 0) totalLuminance / count else 128.0
    }

    fun computeLaplacianVariance(bitmap: Bitmap): Double {
        // Downsample to 128x128 for instantaneous, low-battery edge evaluation
        val scaled = if (bitmap.width > 128 || bitmap.height > 128) {
            Bitmap.createScaledBitmap(bitmap, 128, 128, true)
        } else {
            bitmap
        }

        val width = scaled.width
        val height = scaled.height
        val grayscale = Array(height) { DoubleArray(width) }

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = scaled.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                grayscale[y][x] = 0.299 * r + 0.587 * g + 0.114 * b
            }
        }

        var sum = 0.0
        var sumSq = 0.0
        var count = 0

        // 3x3 Discrete Laplacian Kernel
        // [ 0,  1,  0 ]
        // [ 1, -4,  1 ]
        // [ 0,  1,  0 ]
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val center = grayscale[y][x]
                val up = grayscale[y - 1][x]
                val down = grayscale[y + 1][x]
                val left = grayscale[y][x - 1]
                val right = grayscale[y][x + 1]

                val laplacian = up + down + left + right - 4.0 * center
                sum += laplacian
                sumSq += laplacian * laplacian
                count++
            }
        }

        if (scaled != bitmap) {
            scaled.recycle()
        }

        if (count == 0) return 100.0

        val mean = sum / count
        val variance = (sumSq / count) - (mean * mean)
        return variance.coerceAtLeast(0.0)
    }
}
