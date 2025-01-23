import SwiftUI
import ComposeApp

struct NewTaskView: View {
    @Environment(\.dismiss)
    private var dismiss

    private func cancel() {
       dismiss()
     }

    let viewModel = CreateTaskViewModelHelper.shared.provideCreateTaskViewModel()

    @MainActor
    func observeTaskCreation() async {
        for await uiState in viewModel.uiState {
            if (uiState.taskCreatedSuccessfully) {
                cancel()
            }
        }
    }

    var body: some View {
        Observing(viewModel.uiState) { uiState in
            if #available(iOS 16.0, *) {
                NavigationStack {
                    Form {
                        TextField(
                            "Title",
                            text: Binding(get: { uiState.title }, set: { viewModel.updateTitle(newTitle: $0) })
                        )
                        TextField(
                            "Description",
                            text: Binding(get: { uiState.description_ }, set: { viewModel.updateDescription(newDescription: $0) })
                        )
                    }.toolbar {
                        ToolbarItem(placement: .cancellationAction) {
                            Button(action: cancel) {
                                Text("Cancel")
                            }
                        }
                        ToolbarItem(placement: .confirmationAction) {
                            Button(action: {
                                viewModel.createTask()
                            }) {
                                Text("Add")
                            }
                        }
                    }
                    .navigationBarTitleDisplayMode(.inline)
                    .navigationTitle("New Task")
                }.task {
                    await observeTaskCreation()
                }
            } else {
                // Fallback on earlier versions
            }
        }
      }


}
