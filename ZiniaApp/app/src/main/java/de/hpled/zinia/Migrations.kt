package de.hpled.zinia

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migrations {
    companion object {

        val FROM_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE 'moods' (
                    id INTEGER NOT NULL, 
                    name TEXT NOT NULL,
                    turnOffUnusedDevices INTEGER NOT NULL,
                    taskIds TEXT NOT NULL,
                    PRIMARY KEY(id))
                """.trimIndent())

                database.execSQL("""
                    CREATE TABLE moodTask (
                    id INT NOT NULL,
                    deviceId INT NOT NULL,
                    color INT,
                    PRIMARY KEY(id))
                """.trimIndent())
            }
        }
    }
}