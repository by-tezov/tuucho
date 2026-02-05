import UIKit
import SwiftUI
import ModulesSharedFramework

class ComposeHostController: UIViewController {

    weak var coordinator: AppCoordinator?
    private var composeViewController: UIViewController?
    private var isKoinInitialized = false

    override func viewDidLoad() {
        super.viewDidLoad()
        setupComposeView()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        composeViewController?.view.frame = view.bounds
    }

    func recreateComposeView() {
        cleanup()
        setupComposeView()
    }

    private func cleanup() {
        composeViewController?.willMove(toParent: nil)
        composeViewController?.view.removeFromSuperview()
        composeViewController?.removeFromParent()
        composeViewController = nil
    }

    private func setupComposeView() {
        guard !isKoinInitialized else {
            return
        }
        let vc = KMPKitKt.uiView(koinExtension: { [weak self] koinApplication in
            guard let self = self else {
                return
            }
            let publisher = koinApplication.tuuchoKoinIos.get(clazz: NavigationFinishPublisher.self) as! NavigationFinishPublisher
            publisher.onFinish(block: {
                self.coordinator?.handleKoinClosed()
                koinApplication.tuuchoKoinIos.close()
                self.isKoinInitialized = false
                UIApplication.shared.perform(#selector(NSXPCConnection.suspend))
            })
        })
        self.composeViewController = vc
        self.isKoinInitialized = true
        addChild(vc)
        view.addSubview(vc.view)
        vc.view.frame = view.bounds
        vc.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        vc.didMove(toParent: self)
    }
}
