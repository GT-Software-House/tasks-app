package org.gabrielsantana.tasks.data.source

import io.mockk.mockk
import io.mockk.verify
import org.gabrielsantana.tasks.TasksDatabase
import kotlin.test.Test

class TasksLocalDataSourceTest {

    private val databaseMock = mockk<TasksDatabase>(relaxed = true)
    private val dataSource = TasksLocalDataSource(databaseMock)

    @Test
    fun insertCheckedTaskTest() {
        //when
        dataSource.insert("title", "description", false)

        //then
        verify { databaseMock.taskQueries.insert(null, "title", "description", 0L) }
    }

    @Test
    fun insertUncheckedTaskTest() {
        //when
        dataSource.insert("title", "description", true)

        //then
        verify { databaseMock.taskQueries.insert(null, "title", "description", 1L) }
    }

    @Test
    fun `insert calls queries insert with correct parameters`() {
        // Arrange
        val title = "Test Title"
        val description = "Test Description"
        val isChecked = true

        // Act
        dataSource.insert(title, description, isChecked)

        // Assert
        verify(exactly = 1) {
            databaseMock.taskQueries.insert(
                id = null,
                title = title,
                description = description,
                isChecked = 1L
            )
        }
    }


    @Test
    fun `delete calls queries delete with correct id`() {
        // Arrange
        val id = 123L

        // Act
        dataSource.delete(id)

        // Assert
        verify { databaseMock.taskQueries.delete(id = id) }
    }

    @Test
    fun `updateIsChecked calls queries updateIsChecked with correct parameters`() {
        // Arrange
        val id = 123L
        val isChecked = false

        // Act
        dataSource.updateIsChecked(id, isChecked)

        // Assert
        verify {
            databaseMock.taskQueries.updateIsChecked(
                isChecked = 0L,
                id = id
            )
        }
    }
}