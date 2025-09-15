package com.tezov.tuucho.core.data.repository.network.backendServer.resource.v1

import com.tezov.tuucho.core.data.repository.network.backendServer.resource.JsonResourceProtocol

class ConfigJsonResource : JsonResourceProtocol {

    override val url = "config"

    override val statusCode = 200

    override val jsonString = """
{
  "material-resource": {
    "global": {
      "subs": [
        {
          "validity-key": "2",
          "url": "subs/sub-texts"
        },
        {
          "validity-key": "2",
          "url": "subs/sub-styles"
        },
        {
          "validity-key": "2",
          "url": "subs/sub-contents"
        },
        {
          "validity-key": "2",
          "url": "subs/sub-components"
        }
      ],
      "templates": [
        {
          "validity-key": "2",
          "url": "templates/template-page-default"
        }
      ]
    },
    "local": {
      "pages": [
        {
          "validity-key": "1",
          "url": "page-home"
        },
        {
          "validity-key": "3",
          "url": "page-help",
          "pre-download": false
        }
      ]
    },
    "contextual": {
      "all": [
        {
          "validity-key": "1",
          "urlOrigin": "page-home"
        }
      ]
    }
  }
}
"""
}