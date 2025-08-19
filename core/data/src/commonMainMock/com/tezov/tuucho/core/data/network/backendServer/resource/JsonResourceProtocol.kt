package com.tezov.tuucho.core.data.network.backendServer.resource

interface JsonResourceProtocol {

    val url: String

    val statusCode:Int

    val jsonString: String

}