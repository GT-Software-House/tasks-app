package org.gabrielsantana.tasks.data.driver

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.gabrielsantana.tasks.TasksDatabase

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TasksDatabase.Schema, context, "TasksDatabase")
    }
}