package org.gabrielsantana.tasks

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class IOSDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TasksDatabase.Schema, "TasksDatabase")
    }
}
