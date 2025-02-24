package com.tezov.tuucho.convention.extension

import org.gradle.api.plugins.PluginManager
import org.gradle.plugin.use.PluginDependency

internal fun PluginManager.apply(pluginDependency: PluginDependency) {
    apply(pluginDependency.pluginId)
}
