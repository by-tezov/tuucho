package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.shared.sample._system.Page
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.utils.io.ByteReadChannel

class FailSafePageHttpInterceptor(
    private val config: NetworkRepositoryModule.Config,
) : HttpInterceptor.Node {

    override suspend fun intercept(
        builder: HttpRequestBuilder
    ) {
        val route = builder.url.toString()
            .removePrefix("${config.baseUrl}/")
            .removePrefix("${config.version}/")
            .removePrefix(config.resourceEndpoint)

        if (route != "/${Page.failSafe}") return

//TODO should be a json file from resource
        val fakeJson = """
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
        builder.attributes[HttpInterceptor.LocalResponse] = ByteReadChannel(fakeJson)
    }
}
