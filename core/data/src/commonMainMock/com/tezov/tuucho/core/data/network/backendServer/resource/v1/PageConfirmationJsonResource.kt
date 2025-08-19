package com.tezov.tuucho.core.data.network.backendServer.resource.v1

import com.tezov.tuucho.core.data.network.backendServer.resource.JsonResourceProtocol

class PageConfirmationJsonResource : JsonResourceProtocol {

    override val url = "page-confirmation"

    override val statusCode = 200

    override val jsonString = """
{
  "version": "1",
  "root": {
    "setting": {
      "disable-on-demand-definition-shadower": true
    },
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "background-color": { "id": "*background:funny-color" },
      "fill-max-size": true
    },
    "content": {
      "items": [
        {
          "subset": "label",
          "style": {
            "id": "*title-label"
          },
          "content": {
            "value": "Confirmation:"
          }
        },
        {
          "id": "*item-spacer-half"
        },
        {
          "subset": "label",
          "style": {
            "id": "*title-body",
            "font-color": {"id":"*black"}
          },
          "content": {
            "value": { "default": "Merci, vous avez bien été ajouté à notre newsletter" }
          }
        },
        {
          "id": "*item-spacer"
        },
        {
          "subset": "button",
          "content": {
            "label": {
              "style": { "id": "*label-main-button-back" },
              "content": {
                "value": "Navigate to home page"
              }
            },
            "action": "navigate://local-destination/back"
          }
        }
      ]
    }
  },
  "components": [
    {
      "id": "item-spacer-half",
      "subset": "spacer",
      "style": {
        "weight": "0.5"
      }
    }
  ]
}
"""
}