import SwiftUI
import UIKit
import AppSharedFramework

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        MainScreen_iosKt.uiView()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
