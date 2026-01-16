import UIKit
import SwiftUI
import AppSharedFramework

class ComposeHostController: UIViewController {

    private var publisher: NavigationFinishPublisher!

    override func viewDidLoad() {
        super.viewDidLoad()
        let vc = MainScreen_iosKt.uiView(koinExtension: { [weak self] koinApplication in
            self?.publisher = koinApplication.getNavigationFinishPublisher()
            self?.publisher.onFinish(block: {
                // koin close
                UIApplication.shared.perform(#selector(NSXPCConnection.suspend))
            })
        })
        addChild(vc)
        view.addSubview(vc.view)
        vc.view.frame = view.bounds
        vc.didMove(toParent: self)
    }
}
