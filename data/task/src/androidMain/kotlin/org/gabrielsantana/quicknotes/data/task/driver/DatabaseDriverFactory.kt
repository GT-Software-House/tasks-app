package org.gabrielsantana.quicknotes.data.task.driver

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.gabrielsantana.quicknotes.data.task.TaskDatabase

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TaskDatabase.Schema, context, "TasksDatabase")
    }
}