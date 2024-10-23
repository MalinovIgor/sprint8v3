package ru.startandroid.develop.sprint8v3.library.ui.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.startandroid.develop.sprint8v3.library.ui.db.dao.TrackDao

@Database(
    version = 1,
    entities = [
       TrackEntity::class
    ]
)
abstract class AppDatabase:RoomDatabase() {

    abstract fun trackDao():TrackDao
}