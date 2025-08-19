package com.tezov.tuucho.core.data.network.backendServer.resource.v1

import com.tezov.tuucho.core.data.network.backendServer.resource.JsonResourceProtocol

class ConfigJsonResource : JsonResourceProtocol {

    override val url = "config"

    override val statusCode = 200

    override val jsonString = """
{
  "preload": {
    "subs": [
      {
        "version": "1",
        "url":"subs/sub-texts"
      },
      {
        "version": "1",
        "url":"subs/sub-styles"
      },
      {
        "version": "1",
        "url":"subs/sub-contents"
      },
      {
        "version": "1",
        "url":"subs/sub-components"
      }
    ],
    "templates": [
      {
        "version": "1",
        "url":"templates/template-page-default"
      }
    ],
    "pages": [
      {
        "version": "1",
        "url":"page-home"
      },
      {
        "version": "1",
        "url":"page-help"
      }
    ]  
  }
}
"""
}