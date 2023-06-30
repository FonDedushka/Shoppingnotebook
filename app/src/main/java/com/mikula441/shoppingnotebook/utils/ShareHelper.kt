package com.mikula441.shoppingnotebook.utils

import android.content.Intent
import com.mikula441.shoppingnotebook.entities.ShopListItem

object ShareHelper {
    fun shareShopList(shopList: List<ShopListItem>, lisName: String): Intent{
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList, lisName))
        }
        return intent
    }

    private fun makeShareText(shopList: List<ShopListItem>, lisName: String): String{
        val sBuilder = StringBuilder()
        sBuilder.append("<<$lisName>>")
        sBuilder.append("\n")
        var counter = 0
        shopList.forEach {
            sBuilder.append("${++ counter}. ${it.name}: ${it.itemInfo ?: ""}")
            sBuilder.append("\n")
        }
        return sBuilder.toString()
    }
}