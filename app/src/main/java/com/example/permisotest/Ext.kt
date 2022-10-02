package com.example.permisotest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.Base64

fun Bitmap.Encrypt(): String {

    var stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    var byte = stream.toByteArray()
    android.util.Base64.encodeToString(byte, android.util.Base64.DEFAULT)
    return android.util.Base64.encodeToString(byte, android.util.Base64.DEFAULT)
}

fun String.ToBitmap(): Bitmap {
    var byte = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(byte, 0, byte.size)
}