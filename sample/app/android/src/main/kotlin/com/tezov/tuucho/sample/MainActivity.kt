package com.tezov.tuucho.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import com.tezov.tuucho.sample.di.ApplicationModule
import com.tezov.tuucho.sample.shared.AppScreen
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.NavigationFinishPublisher

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                AppScreen(
                    applicationModules = listOf(ApplicationModule.invoke(applicationContext)),
                    koinExtension = {
                        koin.get<NavigationFinishPublisher>().onFinish {
                            koin.close()
                            this@MainActivity.finish()
                        }
                    }
                )
            }
        }
    }
}
