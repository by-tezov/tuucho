import UIKit
import SwiftUI
import AppSharedFramework

class ComposeHostController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        let vc = MainScreen_iosKt.uiView(koinExtension: { koin in
            let publisher = koin.get(objCClass: NavigationFinishPublisher.self) as! NavigationFinishPublisher
            publisher.onFinish { self.finish() }
        })
        addChild(vc)
        view.addSubview(vc.view)
        vc.view.frame = view.bounds
        vc.didMove(toParent: self)
    }

    private func finish() {
        if let nav = navigationController {
            nav.popViewController(animated: true)
        } else {
            dismiss(animated: true)
        }
    }

}
