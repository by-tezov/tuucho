package com.tezov.tuucho.core.domain.business.jsonSchema.config

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object ConfigSchema {
    object Key {
        const val materialResource = MaterialResource.root
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<Scope>(argument) {
        var materialResource by delegate<JsonObject?>(Key.materialResource)
    }

    object MaterialResource {
        const val root = "material-resource"

        object Key {
            const val global = "global"
            const val local = "local"
            const val contextual = "contextual"
        }

        class Scope(
            argument: SchemaScopeArgument
        ) : OpenSchemaScope<Scope>(argument) {
            override val root = MaterialResource.root

            var global by delegate<JsonObject?>(Key.global)
            var local by delegate<JsonObject?>(Key.local)
            var contextual by delegate<JsonObject?>(Key.contextual)
        }

        object Global {
            object Setting {
                const val root = "setting"

                object Key {
                    const val validityKey = "validity-key"
                    const val urlsWhiteList = "urls-white-list"
                }

                class Scope(
                    argument: SchemaScopeArgument
                ) : OpenSchemaScope<Scope>(argument) {
                    override val root = Setting.root
                    var urlsWhiteList by delegate<JsonArray?>(Key.urlsWhiteList)
                    var validityKey by delegate<String?>(Key.validityKey)
                }
            }

            object Definition {
                const val root = "definition"

                object Key {
                    const val validityKey = "validity-key"
                    const val url = "url"
                }

                class Scope(
                    argument: SchemaScopeArgument
                ) : OpenSchemaScope<Scope>(argument) {
                    override val root = Definition.root
                    var validityKey by delegate<String?>(Key.validityKey)
                    var url by delegate<String?>(Key.url)
                }
            }
        }

        object Local {
            object Setting {
                const val root = "setting"

                object Key {
                    const val validityKey = "validity-key"
                    const val preDownload = "pre-download"
                }

                class Scope(
                    argument: SchemaScopeArgument
                ) : OpenSchemaScope<Scope>(argument) {
                    override val root = Setting.root
                    var validityKey by delegate<String?>(Key.validityKey)
                    var preDownload by delegate<Boolean?>(Key.preDownload)
                }
            }

            object Definition {
                const val root = "definition"

                object Key {
                    const val validityKey = "validity-key"
                    const val url = "url"
                }

                class Scope(
                    argument: SchemaScopeArgument
                ) : OpenSchemaScope<Scope>(argument) {
                    override val root = Definition.root
                    var validityKey by delegate<String?>(Key.validityKey)
                    var url by delegate<String?>(Key.url)
                }
            }
        }

        object Contextual {
            object Setting {
                const val root = "setting"

                object Key {
                    const val validityKey = "validity-key"
                    const val urlOrigin = "url-origin"
                }

                class Scope(
                    argument: SchemaScopeArgument
                ) : OpenSchemaScope<Scope>(argument) {
                    override val root = Setting.root
                    var validityKey by delegate<String?>(Key.validityKey)
                    var urlOrigin by delegate<String?>(Key.urlOrigin)
                }
            }

            object Definition {
                const val root = "definition"

                object Key {
                    const val validityKey = "validity-key"
                    const val url = "url"
                }

                class Scope(
                    argument: SchemaScopeArgument
                ) : OpenSchemaScope<Scope>(argument) {
                    override val root = Definition.root
                    var validityKey by delegate<String?>(Key.validityKey)
                    var url by delegate<String?>(Key.url)
                }
            }
        }
    }
}
