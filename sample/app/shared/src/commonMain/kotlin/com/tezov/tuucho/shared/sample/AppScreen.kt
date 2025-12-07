package com.tezov.tuucho.shared.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import io.kotzilla.sdk.analytics.koin.analytics
import kotlinx.coroutines.delay
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

lateinit var koinLeak: KoinApplication

class A

@Composable
fun AppScreen(
    applicationModules: List<ModuleProtocol>,
) {
    koinLeak = remember {
        startKoin {
            analytics()
            modules(module {
                factory { A() }
            })
        }
    }

    LaunchedEffect(Unit) {
        repeat(100) {
            delay(500)
            koinLeak.koin.get<A>()
        }
    }

//    TuuchoEngineStart(
//        koinModules = SystemSharedModules.invoke() + applicationModules,
//        koinExtension = { analytics() },
//        onStartUrl = "lobby/page-login"
//    )

}
