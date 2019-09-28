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

        val FROM_5_TO_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE 'color_sequence' (
                    id INTEGER NOT NULL, 
                    transitionSpeed FLOAT NOT NULL,
                    keepingTimeMillis INTEGER NOT NULL,
                    colors TEXT NOT NULL,
                    PRIMARY KEY(id))
                """.trimIndent())
            }
        }
    }
}