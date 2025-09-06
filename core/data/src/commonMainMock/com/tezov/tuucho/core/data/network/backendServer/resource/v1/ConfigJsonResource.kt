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
        "validity-key": "1",
        "url":"subs/sub-texts"
      },
      {
        "validity-key": "1",
        "url":"subs/sub-styles"
      },
      {
        "validity-key": "1",
        "url":"subs/sub-contents"
      },
      {
        "validity-key": "1",
        "url":"subs/sub-components"
      }
    ],
    "templates": [
      {
        "validity-key": "1",
        "url":"templates/template-page-default"
      }
    ],
    "pages": [
      {
        "validity-key": "1",
        "url":"page-home"
      },
      {
        "validity-key": "1",
        "url":"page-help"
      }
    ]  
  }
}
"""
}