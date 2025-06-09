package com.pointlessapps.granite.datasource.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class DateSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Long")

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    private val tempDate = Date()

    override fun serialize(encoder: Encoder, value: Long) {
        tempDate.time = value
        encoder.encodeString(dateFormat.format(tempDate))
    }

    override fun deserialize(decoder: Decoder) =
        dateFormat.parse(decoder.decodeString())?.time ?: 0L
}
