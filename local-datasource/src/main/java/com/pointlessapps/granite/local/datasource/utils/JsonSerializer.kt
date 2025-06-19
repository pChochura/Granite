package com.pointlessapps.granite.local.datasource.utils

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.readByteString
import java.io.InputStream
import java.io.OutputStream

internal class JsonSerializer<T>(
    private val json: Json,
    private val serializer: KSerializer<T>,
    override val defaultValue: T,
) : Serializer<T> {

    override suspend fun readFrom(input: InputStream): T = withContext(Dispatchers.IO) {
        val inputJson = input.readByteString(input.available()).utf8()
        json.decodeFromString(serializer, inputJson)
    }

    override suspend fun writeTo(t: T, output: OutputStream) = withContext(Dispatchers.IO) {
        output.writer().apply {
            write(json.encodeToString(serializer, t))
        }.close()
    }
}
