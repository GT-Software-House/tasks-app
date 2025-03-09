package org.gabrielsantana.quicknotes.data.task.driver

import app.cash.sqldelight.db.SqlDriver

internal interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}