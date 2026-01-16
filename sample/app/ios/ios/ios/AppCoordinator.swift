import SwiftUI
import Combine

class AppCoordinator: ObservableObject {

    @Published var shouldRecreateController = false
    private var isKoinClosed = false

    func handleAppAppear() {
        if isKoinClosed {
            shouldRecreateController = true
            isKoinClosed = false
        }
    }

    func handleKoinClosed() {
        isKoinClosed = true
        shouldRecreateController = true
    }
}