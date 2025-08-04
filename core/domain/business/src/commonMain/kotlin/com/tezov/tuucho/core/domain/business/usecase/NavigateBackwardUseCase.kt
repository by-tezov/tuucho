//package com.tezov.tuucho.core.domain.business.usecase
//
//import com.tezov.tuucho.core.domain.business.navigation.protocol.NavigationStackRepositoryProtocol
//import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
//import com.tezov.tuucho.core.domain.business.protocol.ClearTransientMaterialCacheRepositoryProtocol
//import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
//import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
//
//class NavigateBackwardUseCase(
//    private val navigationRepository: NavigationStackRepositoryProtocol,
//    private val viewStackRepository: ViewContextStackRepositoryProtocol,
//) {
//
//    suspend fun invoke(url: String) {
//
////        private val clearTransientMaterialCacheRepository: ClearTransientMaterialCacheRepositoryProtocol,
////        clearTransientMaterialCacheRepository.process(
////            navigationRepository.currentUrl
////        )
//        // TODO done by the viewStackRepository
//
////        renderComponent.invoke(url, component) // Done by the repository
//    }
//
//}