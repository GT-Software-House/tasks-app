package org.gabrielsantana.quicknotes.data.task.source.local.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

class InstantColumnAdapter : ColumnAdapter<Instant, String> {
    override fun decode(databaseValue: String): Instant {
        return Instant.parse(databaseValue)
    }

    override fun encode(value: Instant): String {
        return value.toString()
    }
}