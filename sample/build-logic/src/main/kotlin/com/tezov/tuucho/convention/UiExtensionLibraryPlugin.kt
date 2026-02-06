package com.tezov.tuucho.convention

class UiExtensionLibraryPlugin : AbstractLibraryPlugin() {

    override fun optIn() = listOf<String>(

    ).asIterable()

    override fun compilerOption() = listOf(
        "-Xcontext-parameters", // Needed by Tuucho
    )

    override val hasAssets = false

}

