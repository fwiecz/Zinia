package de.hpled.zinia

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migrations {
    companion object {
        val FROM_1_TO_2 = object : Migration(1, 2) {
            /**
             * Old Tables were not altered so no migration is needed
             */
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
    }
}