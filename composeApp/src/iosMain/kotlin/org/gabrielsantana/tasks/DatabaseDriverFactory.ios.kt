package org.gabrielsantana.tasks

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

// iOS main
actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver =
        NativeSqliteDriver(TasksDatabase.Schema, "TasksDatabase")

}
