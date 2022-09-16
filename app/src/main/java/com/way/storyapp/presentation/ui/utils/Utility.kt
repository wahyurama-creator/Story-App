package com.way.storyapp.presentation.ui.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import com.way.storyapp.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

fun reduceFileImage(file: File): File {
    return file
}

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun isNetworkAvailable(app: Application): Boolean {
    var result = false
    (app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = isCapableNetwork(this, this.activeNetwork)
        } else {
            @Suppress("DEPRECATION") val networkInfo = this.allNetworks
            for (tempNetworkInfo in networkInfo) {
                if (isCapableNetwork(this, tempNetworkInfo))
                    result = true
            }
        }
    }
    return result
}

fun isCapableNetwork(cm: ConnectivityManager, network: Network?): Boolean {
    cm.getNetworkCapabilities(network)?.also {
        if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return true
        } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return true
        }
    }
    return false
}