package com.example.socialprogram.database

//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.sqlite.db.SupportSQLiteDatabase
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//@Database(entities = [User::class], version = 1, exportSchema = false)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "app_database"
//                )
//                    .addCallback(DatabaseCallback()) // Popula o banco com os usuários
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//
//    private class DatabaseCallback : RoomDatabase.Callback() {
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            // Populando o banco com os usuários predefinidos
//            CoroutineScope(Dispatchers.IO).launch {
//                INSTANCE?.userDao()?.apply {
//                    insert(User(email = "usuario1", password = "senha1"))
//                    insert(User(email = "usuario2", password = "senha2"))
//                }
//            }
//        }
//    }
//}