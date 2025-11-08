package com.fitfit.app.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fitfit.app.data.local.dao.ClothesDao
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
    version = 4,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clothesDao(): ClothesDao
    abstract fun outfitDao(): OutfitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
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