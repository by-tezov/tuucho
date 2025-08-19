package com.tezov.tuucho.core.data.network.backendServer.resource.v1

import com.tezov.tuucho.core.data.network.backendServer.resource.JsonResourceProtocol

class PageHelpJsonResource : JsonResourceProtocol {

    override val url = "page-help"

    override val statusCode = 200

    override val jsonString = """
{
  "version": "1",
  "root": {
    "setting": {
      "disable-on-demand-definition-shadower": true
    },
    "id": "*template-page-default"
  },
  "contents": [
    {
      "id": "title-content",
      "value": { "id": "*text-body-title" }
    },
    {
      "id": "body-content",
      "value": { "id": "*text-body-content" }
    },
    {
      "id": "action-content",
      "label": { "id": "*button-label-help-to-home" },
      "action": "navigate://local-destination/back"
    }
  ],
  "texts": {
    "common": {
      "text-body-title": "Looking for help?",
      "text-body-content": { "id": "*text-body-content-help" },
      "text-action-help": "Navigate to home page"
    }
  }
}
"""
}