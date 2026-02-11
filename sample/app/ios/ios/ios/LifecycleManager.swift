import SwiftUI
import Combine

class LifecycleManager: ObservableObject {

    enum State {
        case idle
        case onAppear
        case resumed
        case onSuspend
        case paused
    }

    @Published var state: State = .idle
    private var suspendRequested = false

    func handleOnAppear() {
        guard state == .idle || state == .paused else {
            return
        }
        state = .onAppear
    }

    func suspendRequest() {
        suspendRequested = true
        UIApplication.shared.perform(#selector(NSXPCConnection.suspend))
    }

    func handleOnSuspend() {
        guard suspendRequested && state == .resumed else {
            return
        }
        suspendRequested = false
        state = .onSuspend
    }

}