package com.mikula441.shoppingnotebook.activities

import android.app.Application
import com.mikula441.shoppingnotebook.database.MainDataBase

class MainApp: Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}