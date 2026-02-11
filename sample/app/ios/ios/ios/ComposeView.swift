import SwiftUI
import UIKit

struct ComposeView: UIViewControllerRepresentable {
    @ObservedObject var lifecycleManager: LifecycleManager

    func makeUIViewController(context: Context) -> UIViewController {
        let controller = ComposeHostController()
        controller.lifecycleManager = lifecycleManager
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        guard let controller = uiViewController as? ComposeHostController else {
            return
        }
        if lifecycleManager.state == .onAppear {
            controller.onAppear()
            DispatchQueue.main.async {
                lifecycleManager.state = .resumed
            }
        } else if lifecycleManager.state == .onSuspend {
            controller.onSuspend()
            DispatchQueue.main.async {
                lifecycleManager.state = .paused
            }
        }
    }
}
