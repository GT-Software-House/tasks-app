package org.gabrielsantana.quicknotes.data.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.gabrielsantana.quicknotes.TasksDatabase


class DesktopDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db")
        TasksDatabase.Schema.create(driver)
        return driver
    }
}
