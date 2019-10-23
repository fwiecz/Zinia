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

        val FROM_6_TO_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE 'color_sequence'
                    ADD name TEXT NOT NULL
                    DEFAULT 'Color Sequence'
                """.trimIndent())
            }
        }

        val FROM_7_TO_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE 'color_sequence'
                    RENAME TO 'color_sequence_old'
                """.trimIndent())

                database.execSQL("""
                    CREATE TABLE 'color_sequence' (
                    id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    transitionSpeed INTEGER NOT NULL,
                    keepingTimeMillis INTEGER NOT NULL,
                    colors TEXT NOT NULL,
                    PRIMARY KEY(id))
                """.trimIndent())

                database.execSQL("""
                    DROP TABLE 'color_sequence_old'
                """.trimIndent())
            }
        }

        val FROM_8_TO_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE 'devices'
                    ADD isRGBW INTEGER NOT NULL
                    DEFAULT '0'
                """.trimIndent())
            }
        }
    }
}