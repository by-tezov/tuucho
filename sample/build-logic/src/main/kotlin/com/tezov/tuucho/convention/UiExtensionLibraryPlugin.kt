package com.tezov.tuucho.convention

class UiExtensionLibraryPlugin : AbstractLibraryPlugin() {

    override fun optIn() = listOf<String>(

    ).asIterable()

    override fun compilerOption() = listOf<String>(
        "-Xcontext-parameters", // Needed by Tuucho
    )

    override val hasAssets = false

}

