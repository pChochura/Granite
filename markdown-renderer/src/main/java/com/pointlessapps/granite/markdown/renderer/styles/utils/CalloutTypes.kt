package com.pointlessapps.granite.markdown.renderer.styles.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import com.pointlessapps.granite.markdown.renderer.R

internal object CalloutTypes {

    fun getColor(calloutType: String) = when (calloutType) {
        "abstract", "summary", "tldr" -> Color(83, 223, 221)
        "info" -> Color(2, 122, 255)
        "todo" -> Color(2, 122, 255)
        "tip", "hint", "important" -> Color(83, 223, 221)
        "success", "check", "done" -> Color(68, 207, 110)
        "question", "help", "faq" -> Color(233, 151, 63)
        "warning", "caution", "attention" -> Color(233, 151, 63)
        "failure", "fail", "missing" -> Color(251, 70, 76)
        "danger", "error" -> Color(251, 70, 76)
        "bug" -> Color(251, 70, 76)
        "example" -> Color(168, 130, 255)
        "quote" -> Color(158, 158, 158)
        else -> Color(2, 122, 255)
    }

    fun getIconId(calloutType: String) = when (calloutType) {
        "abstract", "summary", "tldr" -> R.drawable.ic_summary
        "info" -> R.drawable.ic_info
        "todo" -> R.drawable.ic_todo
        "tip", "hint", "important" -> R.drawable.ic_tip
        "success", "check", "done" -> R.drawable.ic_success
        "question", "help", "faq" -> R.drawable.ic_question
        "warning", "caution", "attention" -> R.drawable.ic_warning
        "failure", "fail", "missing" -> R.drawable.ic_fail
        "danger", "error" -> R.drawable.ic_danger
        "bug" -> R.drawable.ic_bug
        "example" -> R.drawable.ic_example
        "quote" -> R.drawable.ic_quote
        else -> R.drawable.ic_note
    }

    fun getCalloutType(context: Context, calloutType: String, lineHeight: Int) = CalloutType(
        color = getColor(calloutType),
        icon = convertToBitmap(
            drawable = context.getDrawable(getIconId(calloutType))!!,
            widthPixels = lineHeight,
            heightPixels = lineHeight,
        ),
    )

    private fun convertToBitmap(drawable: Drawable, widthPixels: Int, heightPixels: Int): Bitmap {
        val bitmap = createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, widthPixels, heightPixels)
        drawable.draw(canvas)
        return bitmap
    }

    data class CalloutType(
        val color: Color,
        val icon: Bitmap,
    )
}
