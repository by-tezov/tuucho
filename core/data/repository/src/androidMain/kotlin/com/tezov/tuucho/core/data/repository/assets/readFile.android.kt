package com.tezov.tuucho.core.data.repository.assets

import android.content.Context
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid
import org.koin.java.KoinJavaComponent.get

actual fun readResourceFile(path: String): String {
    val context: Context = get(
        clazz = Context::class.java,
        qualifier = DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT
    )
    return context.assets.open("files/$path").bufferedReader().use { it.readText() }
}