package com.mikula441.shoppingnotebook.utils

import android.text.Html
import android.text.Spanned

object HtmlManager {
    fun getFromHtml(text: String): Spanned{
       return if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N) {
           @Suppress("DEPRECATION")
           Html.fromHtml(text)
       } else {
           Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
       }
    }

    fun toHtml(text: Spanned): String{
        return if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            Html.toHtml(text)
        } else {
            Html.toHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}