import SwiftUI
import UIKit

struct ComposeView: UIViewControllerRepresentable {
    @ObservedObject var coordinator: AppCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        let controller = ComposeHostController()
        controller.coordinator = coordinator
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        if coordinator.shouldRecreateController {
            guard let controller = uiViewController as? ComposeHostController else { return }
            controller.recreateComposeView()
            DispatchQueue.main.async {
                coordinator.shouldRecreateController = false
            }
        }
    }
}
