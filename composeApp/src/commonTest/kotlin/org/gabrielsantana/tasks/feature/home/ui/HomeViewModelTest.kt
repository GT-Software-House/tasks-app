package org.gabrielsantana.tasks.feature.home.ui

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.gabrielsantana.tasks.Task
import org.gabrielsantana.quicknote.data.task.data.TasksRepository
import org.gabrielsantana.tasks.features.home.ui.HomeUiState
import org.gabrielsantana.tasks.features.home.ui.HomeViewModel
import org.gabrielsantana.tasks.features.home.ui.TaskUiModel
import org.gabrielsantana.tasks.features.home.ui.toUiModel
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tasksRepository: TasksRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        tasksRepository = mockk(relaxed = true)
        viewModel = HomeViewModel(tasksRepository)
    }

    @Test
    fun `init loads tasks`() = runTest {
        // Arrange
        val tasks = listOf(
            Task(1, "Task 1", "Description 1", 0),
            Task(2, "Task 2", "Description 2", 1)
        )
        every { tasksRepository.getTasks() } returns flowOf(tasks)

        // Act
        advanceUntilIdle()

        // Assert
        val expectedUiState = HomeUiState(
            tasks = tasks.map { it.toUiModel() }
        )
        assertEquals(expectedUiState.tasks, viewModel.uiState.value.tasks)
    }

    @Test
    fun `selectTask adds or removes task index`() {
        // Arrange
        val taskIndex = 1

        // Act
        viewModel.selectTask(taskIndex)
        var state = viewModel.uiState.value
        assert(state.selectedTasksIndex.contains(taskIndex))

        viewModel.selectTask(taskIndex)
        state = viewModel.uiState.value

        // Assert
        assert(!state.selectedTasksIndex.contains(taskIndex))
    }

    @Test
    fun `clearSelectedTasks clears all selected tasks`() {
        // Arrange
        viewModel.selectTask(1)
        viewModel.selectTask(2)

        // Act
        viewModel.clearSelectedTasks()
        val state = viewModel.uiState.value

        // Assert
        assert(state.selectedTasksIndex.isEmpty())
    }

    @Test
    fun `deleteSelectedTasks deletes tasks and clears selection`() {
        // Arrange
        val tasks = listOf(
            TaskUiModel(1, "Task 1", "Description 1", false),
            TaskUiModel(2, "Task 2", "Description 2", true)
        )
        val taskIds = tasks.map { it.id.toLong() }
        val uiState = HomeUiState(tasks = tasks, selectedTasksIndex = setOf(0, 1))
        every { tasksRepository.deleteTask(any()) } just Runs

        // Act
        viewModel.selectTask(0)
        viewModel.selectTask(1)
        viewModel.deleteSelectedTasks()

        // Assert
        verify(exactly = 1) { tasksRepository.deleteTask(taskIds[0]) }
        verify(exactly = 1) { tasksRepository.deleteTask(taskIds[1]) }
        assert(viewModel.uiState.value.selectedTasksIndex.isEmpty())
    }

    @Test
    fun `searchTasks filters tasks by query`() = runTest {
        // Arrange
        val tasks = listOf(
            Task(1, "Buy groceries", "Description 1", 0),
            Task(2, "Call mom", "Description 2", 1),
            Task(3, "Gym workout", "Description 3", 1)
        )
        every { tasksRepository.getTasks() } returns flowOf(tasks)

        // Act
        viewModel.searchTasks("Call")
        advanceUntilIdle()

        // Assert
        val filteredTasks = listOf(tasks[1].toUiModel())
        assertEquals(filteredTasks, viewModel.uiState.value.tasks)
    }

    @Test
    fun `updateTask updates task status`() = runTest {
        // Arrange
        val task = TaskUiModel(1, "Task 1", "Description 1", false)
        every { tasksRepository.updateTask(any(), any()) } just Runs

        // Act
        viewModel.updateTask(true, task)

        // Assert
        verify { tasksRepository.updateTask(task.id.toLong(), true) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
