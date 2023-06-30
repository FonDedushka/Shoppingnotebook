package com.mikula441.shoppingnotebook.database

import android.content.Context
//import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mikula441.shoppingnotebook.entities.LibraryItem
import com.mikula441.shoppingnotebook.entities.NoteItem
import com.mikula441.shoppingnotebook.entities.ShopListNameItem
import com.mikula441.shoppingnotebook.entities.ShopListItem

@Database(entities = [LibraryItem::class, NoteItem::class,
    ShopListItem::class, ShopListNameItem::class],
    version = 1
)
abstract class MainDataBase: RoomDatabase() {
    abstract fun getDao(): Dao
    companion object{
        @Volatile
        private var INSTANCE: MainDataBase? = null
        fun getDataBase(context: Context): MainDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shopping_list_db"
                ).build()
                instance
            }
        }
    }
}