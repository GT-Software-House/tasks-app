package org.gabrielsantana.tasks

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver


class DesktopDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TasksDatabase.Schema.create(driver)
        return driver
    }
}
