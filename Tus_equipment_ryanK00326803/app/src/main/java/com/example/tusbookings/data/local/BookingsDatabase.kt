package com.example.tusbookings.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tusbookings.model.Item
import com.example.tusbookings.model.ItemType
import com.example.tusbookings.model.Loan

@Database(
    entities = [ItemType::class, Item::class, Loan::class],
    version = 1,
    exportSchema = true
)
abstract class BookingsDatabase : RoomDatabase() {
    abstract fun bookingsDao(): BookingsDao

    companion object {
        @Volatile private var instance: BookingsDatabase? = null

        fun getInstance(context: Context): BookingsDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BookingsDatabase::class.java,
                    "tusbookings.db"
                ).build().also { instance = it }
            }
    }
}
