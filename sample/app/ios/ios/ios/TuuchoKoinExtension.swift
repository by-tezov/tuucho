import ModulesSharedFramework

protocol TuuchoKoinIosComponent {
}

extension TuuchoKoinIosComponent {
    func getKoinIos() -> CoreKoinIos {
        return KMPKitKt.koinIsolatedContext.koin.iOS
    }
}