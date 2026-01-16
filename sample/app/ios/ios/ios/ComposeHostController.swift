import UIKit
import SwiftUI
import AppSharedFramework

class ComposeHostController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        let vc = KMPKitKt.uiView(koinExtension: { [weak self] koinApplication in
            let publisher = koinApplication.tuuchoKoinIos.get(clazz: NavigationFinishPublisher.self) as! NavigationFinishPublisher
            publisher.onFinish(block: {
                 koinApplication.koinIos.close()
                 UIApplication.shared.perform(#selector(NSXPCConnection.suspend))
            })
        })
        addChild(vc)
        view.addSubview(vc.view)
        vc.view.frame = view.bounds
        vc.didMove(toParent: self)
    }
}
