package com.tezov.tuucho.shared.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import io.kotzilla.sdk.KotzillaCore
import io.kotzilla.sdk.KotzillaCoreSDK
import io.kotzilla.sdk.analytics.koin.analytics
import io.kotzilla.sdk.analytics.koin.analyticsLogger
import io.kotzilla.sdk.android.security.apiKey
import io.kotzilla.sdk.getVersionName
import kotlinx.coroutines.delay
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

class A

@Composable
fun AppScreen(
    applicationModules: List<ModuleProtocol>,
) {
    val context = LocalContext.current
    val koinLeak: KoinApplication = remember {
        startKoin {
            //    analyticsLogger(sdkInstance = instance)
            modules(module {
                factory { A() }
            })
        }
    }
    val instance: KotzillaCore = remember {
        KotzillaCoreSDK()
            .setup(context.apiKey(), context.getVersionName())
            .attachDefaultKoin()
            .connect()
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
