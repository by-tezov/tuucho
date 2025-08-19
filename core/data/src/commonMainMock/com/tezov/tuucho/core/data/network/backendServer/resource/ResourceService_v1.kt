package com.tezov.tuucho.core.data.network.backendServer.resource

import com.tezov.tuucho.core.data.network.backendServer.BackendServer
import com.tezov.tuucho.core.data.network.backendServer.protocol.ServiceProtocol
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.ConfigJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageAJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageBJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageCJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageConfirmationJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageDJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageEJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageHelpJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageHomeJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.PageHomeOnDemandDefinitionJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.subs.SubComponentsJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.subs.SubContentsJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.subs.SubStylesJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.subs.SubTextsJsonResource
import com.tezov.tuucho.core.data.network.backendServer.resource.v1.templates.TemplatePageDefaultJsonResource
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets

class ResourceService_v1 : ServiceProtocol {

    private val jsons = listOf(
        //subs
        SubComponentsJsonResource(),
        SubContentsJsonResource(),
        SubStylesJsonResource(),
        SubTextsJsonResource(),
        //templates
        TemplatePageDefaultJsonResource(),
        //root
        ConfigJsonResource(),
        PageHomeJsonResource(),
        PageHomeOnDemandDefinitionJsonResource(),
        PageHelpJsonResource(),
        PageConfirmationJsonResource(),
        PageAJsonResource(),
        PageBJsonResource(),
        PageCJsonResource(),
        PageDJsonResource(),
        PageEJsonResource(),
    )

    override val url = "resource"

    override val version = "v1"

    override fun process(request: BackendServer.Request): BackendServer.Response {
        return jsons.first { it.url == request.url }.let {
            BackendServer.Response(
                statusCode = HttpStatusCode.Companion.fromValue(it.statusCode),
                headers = headersOf(
                    name = HttpHeaders.ContentType,
                    value = ContentType.Application.Json.withCharset(Charsets.UTF_8).toString()
                ),
                body = it.jsonString
            )
        }
    }
}