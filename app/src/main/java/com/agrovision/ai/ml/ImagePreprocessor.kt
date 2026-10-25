package com.agrovision.ai.ml

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * High-performance image preprocessor for the EfficientNet-B3 Dual-Head on-device model.
 * Resizes the input image to 224x224 and extracts raw RGB float values [0.0, 255.0]
 * matching the ImageNet-pretrained EfficientNet-B3 input contract.
 */
object ImagePreprocessor {

    const val INPUT_SIZE = 224
    private const val CHANNELS = 3
    private const val BYTES_PER_FLOAT = 4

    /**
     * Preprocesses a given Bitmap into a direct ByteBuffer for TFLite inference.
     *
     * @param bitmap Raw input image Bitmap
     * @return ByteBuffer of capacity 1 * 224 * 224 * 3 * 4 bytes with native byte order
     */
    fun preprocess(bitmap: Bitmap): ByteBuffer {
        val scaledBitmap = if (bitmap.width == INPUT_SIZE && bitmap.height == INPUT_SIZE) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        }

        val buffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * CHANNELS * BYTES_PER_FLOAT).apply {
            order(ByteOrder.nativeOrder())
            rewind()
        }

        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaledBitmap.getPixels(intValues, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        // EfficientNet-B3 internal rescaling takes [0.0, 255.0] RGB floats
        for (pixelValue in intValues) {
            val r = ((pixelValue shr 16) and 0xFF).toFloat()
            val g = ((pixelValue shr 8) and 0xFF).toFloat()
            val b = (pixelValue and 0xFF).toFloat()

            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }

        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }

        buffer.rewind()
        return buffer
    }
}
