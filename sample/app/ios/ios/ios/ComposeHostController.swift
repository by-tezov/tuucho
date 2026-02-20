import UIKit
import SwiftUI
import ModulesSharedFramework

class ComposeHostController: UIViewController, TuuchoKoinIosComponent {
    weak var lifecycleManager: LifecycleManager?
    private var composeViewController: UIViewController?
    private var bottomConstraint: NSLayoutConstraint?

    override func viewDidLoad() {
        super.viewDidLoad()
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillChangeFrame),
            name: UIResponder.keyboardWillChangeFrameNotification,
            object: nil
        )
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

        vc.view.translatesAutoresizingMaskIntoConstraints = false
        bottomConstraint = vc.view.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        NSLayoutConstraint.activate([
                                        vc.view.topAnchor.constraint(equalTo: view.topAnchor),
                                        vc.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                                        vc.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
                                        bottomConstraint!
                                    ])
        vc.didMove(toParent: self)
    }

    func onSuspend() {
        getKoinIos().close()
        composeViewController?.willMove(toParent: nil)
        composeViewController?.view.removeFromSuperview()
        composeViewController?.removeFromParent()
        composeViewController = nil
    }

    @objc
    private func keyboardWillChangeFrame(notification: Notification) {
        guard
            let userInfo = notification.userInfo,
            let frame = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect,
            let duration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? Double
        else {
            return
        }

        let keyboardFrame = view.convert(frame, from: nil)
        let intersection = view.bounds.intersection(keyboardFrame)
        let height = intersection.height

        bottomConstraint?.constant = -height

        UIView.animate(withDuration: duration) {
            self.view.layoutIfNeeded()
        }
    }
}
