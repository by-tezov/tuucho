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
            uiViewController.recreateComposeView()
            coordinator.shouldRecreateController = false
        }
    }
}
