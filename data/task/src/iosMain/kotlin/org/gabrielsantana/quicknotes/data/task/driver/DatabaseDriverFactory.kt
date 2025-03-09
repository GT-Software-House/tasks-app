package org.gabrielsantana.quicknotes.data.task.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.gabrielsantana.quicknotes.data.task.TaskDatabase

class IOSDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TaskDatabase.Schema, "TasksDatabase")
    }
}
