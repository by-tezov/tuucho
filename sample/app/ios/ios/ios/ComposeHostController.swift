import UIKit
import SwiftUI
import ModulesSharedFramework

class ComposeHostController: UIViewController, TuuchoKoinIosComponent {
    weak var lifecycleManager: LifecycleManager?
    private var composeViewController: UIViewController?

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        composeViewController?.view.frame = view.bounds
    }

    func onAppear() {
        guard composeViewController == nil else {
            return
        }
        let vc = KMPKitKt.uiView(koinExtension: { [weak self] koinApplication in
            let publisher = koinApplication.koin.iOS.get(clazz: NavigationFinishPublisher.self) as! NavigationFinishPublisher
            publisher.onFinish(block: {
                DispatchQueue.main.async {
                    self?.lifecycleManager?.suspendRequest()
                }
            })
        })
        composeViewController = vc
        addChild(vc)
        view.addSubview(vc.view)
        vc.view.frame = view.bounds
        vc.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        vc.didMove(toParent: self)
    }

    func onSuspend() {
        getKoinIos().close()
        composeViewController?.willMove(toParent: nil)
        composeViewController?.view.removeFromSuperview()
        composeViewController?.removeFromParent()
        composeViewController = nil
    }
}
