package com.tezov.tuucho.core.data.repository.network.backendServer.resource.v1.templates

import com.tezov.tuucho.core.data.repository.network.backendServer.resource.JsonResourceProtocol

class TemplatePageDefaultJsonResource : JsonResourceProtocol {

    override val url = "templates/template-page-default"

    override val statusCode = 200

    override val jsonString = """
{
  "root": {
    "id": "template-page-default",
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "background-color": { "id": "*background:why-not-color" },
      "fill-max-size": true
    },
    "content": {
      "items": [
        {
          "subset": "label",
          "style": {
            "id": "*title-label",
            "font-color": "#FF78FF78"
          },
          "content": {
            "id": "*title-content"
          }
        },
        {
          "id": "*item-spacer"
        },
        {
          "subset": "label",
          "style": {
            "font-size": "18",
            "font-color": {"id":"*blue"}
          },
          "content": {
            "id": "*body-content"
          }
        },
        {
          "id": "*item-spacer"
        },
        {
          "subset": "button",
          "content": {
            "id": "*action-content"
          }          
        }
      ]
    }
  }
}
"""
}