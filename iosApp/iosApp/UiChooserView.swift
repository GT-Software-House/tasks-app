import Foundation
import SwiftUI

struct UiChooserView: View {
    var body: some View {
        NavigationView {
            VStack {
                NavigationLink(destination: HomeView().navigationBarBackButtonHidden(true)) {
                    Text("Native UI")
                }
                Spacer().frame(height: 8)
                NavigationLink(destination: ComposeAppView().navigationBarBackButtonHidden(true)) {
                    Text("Compose UI")
                }
            }
        }
    }
}
