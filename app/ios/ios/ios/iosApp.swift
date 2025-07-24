import SwiftUI
import AppKmmFramework

@main
struct iosApp: App {

    init() {
        KmmApplicationKt.onCreate()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

}
