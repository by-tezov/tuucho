import SwiftUI
import UIKit

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        ComposeHostController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
