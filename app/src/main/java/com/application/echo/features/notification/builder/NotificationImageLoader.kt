package com.application.echo.features.notification.builder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Downloads and processes images for notifications.
 *
 * Runs on whatever thread calls it — the caller (service) is
 * already on a background thread from Firebase.
 */
@Singleton
class NotificationImageLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * Downloads an image and crops it into a circle for use as
     * a notification avatar / large icon.
     */
    fun loadCircularAvatar(url: String?, sizePx: Int = AVATAR_SIZE_PX): Bitmap? {
        val bitmap = downloadBitmap(url) ?: return null
        return cropCircular(scaleBitmap(bitmap, sizePx))
    }

    /**
     * Downloads an image for use in BigPictureStyle notifications.
     */
    fun loadImage(url: String?): Bitmap? = downloadBitmap(url)

    private fun downloadBitmap(url: String?): Bitmap? {
        if (url.isNullOrBlank()) return null
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(input)
            input.close()
            connection.disconnect()
            bitmap
        } catch (e: Exception) {
            Timber.w(e, "Failed to download notification image: %s", url)
            null
        }
    }

    private fun scaleBitmap(source: Bitmap, sizePx: Int): Bitmap {
        if (source.width == sizePx && source.height == sizePx) return source
        val scale = sizePx.toFloat() / minOf(source.width, source.height)
        val scaledWidth = (source.width * scale).toInt()
        val scaledHeight = (source.height * scale).toInt()
        val scaled = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true)
        // Center-crop to square
        val x = (scaled.width - sizePx) / 2
        val y = (scaled.height - sizePx) / 2
        return Bitmap.createBitmap(scaled, x, y, sizePx, sizePx)
    }

    private fun cropCircular(source: Bitmap): Bitmap {
        val size = minOf(source.width, source.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, size, size)

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(source, rect, rect, paint)

        return output
    }

    companion object {
        private const val AVATAR_SIZE_PX = 256
        private const val TIMEOUT_MS = 5_000
    }
}
