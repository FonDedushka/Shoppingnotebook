package com.mikula441.shoppingnotebook.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping_list_names")
data class ShopListNameItem (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "allItemCounter")
    val allItemCounter: Int,
    @ColumnInfo(name = "checkedItemCounter")
    val checkedItemCounter: Int,
    @ColumnInfo(name = "itemsId")
    val itemsId: String
    ): Serializable