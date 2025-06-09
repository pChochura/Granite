package com.pointlessapps.granite.utils

import android.os.Parcel
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.parcelize.Parceler

internal object TextFieldValueParceler : Parceler<TextFieldValue> {
    override fun TextFieldValue.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.text)
        parcel.writeInt(this.selection.start)
        parcel.writeInt(this.selection.end)
    }

    override fun create(parcel: Parcel) = TextFieldValue(
        text = parcel.readString().orEmpty(),
        selection = TextRange(parcel.readInt(), parcel.readInt()),
    )
}
