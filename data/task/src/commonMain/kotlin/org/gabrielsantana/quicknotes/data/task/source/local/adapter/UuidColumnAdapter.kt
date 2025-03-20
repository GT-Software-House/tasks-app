package org.gabrielsantana.quicknotes.data.task.source.local.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlin.uuid.Uuid

internal class UuidColumnAdapter : ColumnAdapter<Uuid, String> {
    override fun decode(databaseValue: String): Uuid {
        return Uuid.parse(databaseValue)
    }

    override fun encode(value: Uuid): String {
        return value.toString()
    }
}