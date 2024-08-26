package io.github.qingshu.ayaka.example.yolo.extensions

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader

fun InputStream.toByteArray(): ByteArray {
    ByteArrayOutputStream().use { bos ->
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (this.read(buffer).also { bytesRead = it } != -1) {
            bos.write(buffer, 0, bytesRead)
        }
        return bos.toByteArray()
    }
}

fun InputStream.toBufferedReader(): BufferedReader {
    return BufferedReader(InputStreamReader(this))
}