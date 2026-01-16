import SwiftUI

@main
struct iosApp: App {
    @StateObject private var coordinator = AppCoordinator()

    var body: some Scene {
        WindowGroup {
            ComposeView(coordinator: coordinator)
                .onAppear {
                    coordinator.handleAppAppear()
                }
        }
    }

}
