package com.fitfit.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fitfit.app.data.local.converters.ListConverter
import com.fitfit.app.data.local.dao.ClothesDao
import com.fitfit.app.data.local.dao.OutfitClothesDao
import com.fitfit.app.data.local.dao.OutfitDao
import com.fitfit.app.data.local.dao.UserDao
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.local.entity.OutfitClothesCrossRef
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ClothesEntity::class,
        OutfitEntity::class,
        OutfitClothesCrossRef::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clothesDao(): ClothesDao
    abstract fun outfitDao(): OutfitDao
    abstract fun outfitClothesDao(): OutfitClothesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitfit_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}