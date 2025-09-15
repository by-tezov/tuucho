package com.tezov.tuucho.core.data.repository.network.backendServer.resource.v1.subs

import com.tezov.tuucho.core.data.repository.network.backendServer.resource.JsonResourceProtocol

class SubComponentsJsonResource : JsonResourceProtocol {

    override val url = "subs/sub-components"

    override val statusCode = 200

    override val jsonString = """
{
  "components": [
    {
      "id": "item-spacer",
      "subset": "spacer",
      "style": {
        "weight": "1.0"
      }
    },
    {
      "id": "item-spacer-48-v",
      "subset": "spacer",
      "style": {
        "id": "*item-spacer-48-v"
      }
    },
    {
      "id": "item-spacer-24-v",
      "subset": "spacer",
      "style": {
        "id": "*item-spacer-24-v"
      }
    },
    {
      "id": "button-label-help-to-home",
      "content": {
        "value": { "id": "*text-action-help" }
      }
    }
  ]
}
"""
}