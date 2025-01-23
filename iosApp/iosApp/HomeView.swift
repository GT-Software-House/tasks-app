import UIKit
import SwiftUI
import ComposeApp

struct HomeView: View {
    @State
    private var showCreateTaskDialog = false
    let viewModel = HomeViewModelHelper.shared.provideHomeViewModel()
    @State
    private var searchText: String = ""

    var body: some View {
        Observing(viewModel.uiState) { uiState in
            NavigationView {
                List(uiState.tasks) { task in
                    HStack {
                        VStack(alignment: .leading) {
                            Text(task.title).font(.system(size: 18))
                            Spacer().frame(height: 4)
                            Text(task.description_).font(.system(size: 14))
                        }
                        Spacer()
                        Toggle(
                            isOn: Binding(
                                get: {
                                    task.isChecked
                                }, set: { _ in
                                viewModel.updateTask(isChecked: !task.isChecked, task: task)
                            }
                            )
                        ) {
                        }
                        .labelsHidden().toggleStyle(CheckboxStyle())

                    }
                    .padding(.all, 8)

                }
                .navigationTitle("Tasks")
                .toolbar {
                    ToolbarItemGroup(placement: .bottomBar) {
                        Button(action: {
                            showCreateTaskDialog.toggle()
                        }) {
                            HStack {
                                Image(systemName: "plus.circle.fill")
                                Text("New Reminder")
                            }
                        }
                        Spacer()
                    }
                }
                .sheet(isPresented: $showCreateTaskDialog) {
                    NewTaskView()
                }
            }.searchable(text: Binding(get: {
                searchText
            }, set: { newValue in
                self.searchText = newValue
                viewModel.searchTasks(query: newValue)
            }))

        }
    }
}

struct CheckboxStyle: ToggleStyle {

    func makeBody(configuration: Self.Configuration) -> some View {

        return HStack {
            Image(systemName: configuration.isOn ? "checkmark.square" : "square")
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundColor(configuration.isOn ? .blue : .gray)
                .font(.system(size: 20, weight: .regular, design: .default))
            configuration.label
        }
        .onTapGesture {
            configuration.isOn.toggle()
        }

    }
}

extension TaskUiModel: @retroactive Identifiable {
}



