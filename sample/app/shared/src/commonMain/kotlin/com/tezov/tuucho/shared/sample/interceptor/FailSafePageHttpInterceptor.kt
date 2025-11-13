package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.shared.sample._system.Page
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel

class FailSafePageHttpInterceptor(
    private val config: NetworkRepositoryModule.Config,
) : HttpInterceptor {
    override suspend fun process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>
    ): HttpResponseData {
        val route = context.builder.url.toString()
            .removePrefix("${config.baseUrl}/")
            .removePrefix("${config.version}/")
            .removePrefix(config.resourceEndpoint)

        if (route != "/${Page.failSafe}") return next.invoke(context)

        val localResponse = HttpResponseData(
            statusCode = HttpStatusCode.OK,
            requestTime = GMTDate(),
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
            version = HttpProtocolVersion.HTTP_1_1,
            body = ByteReadChannel(failSafeResponse),
            callContext = context.builder.executionContext
        )
        return localResponse
    }

    private val failSafeResponse
        get() = """
{
  "setting": {
    "ttl": { "strategy": "single-use" }
  },
  "root": {
    "setting": {
      "navigation": {
        "definition": {
          "option": {
            "single": true,
            "clear-stack": true
          },
          "transition": "fade"
        }
      }
    },
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "fill-max-size": true
    },
    "content": {
      "items": [
        {
          "subset": "spacer",
          "style": {
            "weight": "0.5"
          }
        },
        {
          "subset": "label",
          "content": {
            "value": "Sorry your app in not available, check your internet connection"
          }
        },
        {
          "subset": "spacer",
          "style": {
            "weight": "1.0"
          }
        }
      ]
    }
  }
}
        """.trimIndent()
}
