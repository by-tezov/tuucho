package com.tezov.tuucho.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.tezov.tuucho.sample.di.ApplicationModule
import com.tezov.tuucho.shared.sample.AppScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppScreen(listOf(ApplicationModule.invoke(applicationContext)))
        }
    }

}
