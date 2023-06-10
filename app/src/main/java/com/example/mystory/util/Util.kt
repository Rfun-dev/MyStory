package com.example.mystory.util

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.example.mystory.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

object Util {
    private const val FILEFNAME_FORMAT = "dd-MMMM-yyyy"
    private const val DATE_DD_MMMM_YYYY_FULL_FORMAT = "dd MMMM yyyy - HH:mm"

    private val timeStamp = SimpleDateFormat(
        FILEFNAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    fun showNotification(message: String, icon: Int, context: Context): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.info))
            .setMessage(message)
            .setIcon(icon)
            .setNegativeButton(context.getString(R.string.close)
            ) { dialog, _ -> dialog?.dismiss() }
            .create()
    }

    fun createFile(application: Application) : File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it,application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        val outputDirectory = if(
            mediaDir != null && mediaDir.exists()
        )mediaDir else application.filesDir

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

    fun Bitmap.bitmapToFile(context: Context) : File{
        val wrapper = ContextWrapper(context)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.JPEG,25,stream)
        stream.flush()
        stream.close()
        return file
    }

    fun uriToFile(selectedImg : Uri, context: Context) : File{
        val contentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream : OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len : Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf,0,len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun reduceFileImage(file: File) : File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formattedDate(currentDateString: String, targetZone: String): String {
        val instant = Instant.parse(currentDateString)
        val outputFormatter = DateTimeFormatter.ofPattern(DATE_DD_MMMM_YYYY_FULL_FORMAT)
            .withZone(ZoneId.of(targetZone))
        return outputFormatter.format(instant)
    }

}