import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    //TODO init firebase auth too
    init() {
        KoinHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            UiChooserView()
        }
    }
}
