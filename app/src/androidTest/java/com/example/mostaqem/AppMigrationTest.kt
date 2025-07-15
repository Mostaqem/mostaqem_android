package com.example.mostaqem

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mostaqem.core.database.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppMigrationTest {
    private val DB_NAME = "database"

    private val ALL_MIGRATIONS = arrayOf(
        AppDatabase.Companion.migrate2To3
    )

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java, emptyList(),
            FrameworkSQLiteOpenHelperFactory()
        )

    @Test
    @Throws(IOException::class)
    fun testMigration2To3() {
        helper.createDatabase(DB_NAME, 2).apply { close() }
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java, DB_NAME
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}