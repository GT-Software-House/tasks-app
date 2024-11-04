package org.gabrielsantana.tasks

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): TasksDatabase {
    val driver = driverFactory.create()
    val database = TasksDatabase(driver)
    return database
    // Do more work with the database (see below).
}

