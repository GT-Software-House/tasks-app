package org.gabrielsantana.tasks.data.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.gabrielsantana.tasks.TasksDatabase

class IOSDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TasksDatabase.Schema, "TasksDatabase")
    }
}
