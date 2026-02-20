import SwiftUI

@main
struct iosApp: App {
    @StateObject private var lifecycleManager = LifecycleManager()

    var body: some Scene {
        WindowGroup {
            ComposeView(lifecycleManager: lifecycleManager)
            .ignoresSafeArea(.keyboard)
                .onAppear {
                    lifecycleManager.handleOnAppear()
                }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.willEnterForegroundNotification)) { _ in
                    lifecycleManager.handleOnAppear()
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.didEnterBackgroundNotification)) { _ in
                lifecycleManager.handleOnSuspend()
            }
        }
    }
}
