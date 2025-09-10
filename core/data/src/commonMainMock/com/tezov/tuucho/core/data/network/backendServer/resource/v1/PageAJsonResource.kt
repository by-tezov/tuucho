package com.tezov.tuucho.core.data.network.backendServer.resource.v1

import com.tezov.tuucho.core.data.network.backendServer.resource.JsonResourceProtocol

class PageAJsonResource : JsonResourceProtocol {

    override val url = "page-a"

    override val statusCode = 200

    override val jsonString = """
{
  "setting": {
    "ttl": {
      "strategy": "transient",
      "transient-value": "10s"
    }
  },
  "root": {
    "setting": {
      "disable-contextual-shadower": true,
      "navigation": {
        "extra": {
          "is-background-solid": true
        },
        "definition": [
          {
            "selector": {
              "type": "page-bread-crumb",
              "value": "page-c"
            },
            "transition": {
              "type": "slide-vertical",
              "entrance": "from-bottom",
              "effect": "cover"
            }
          },
          {
            "option": {
              "single": true,
              "reuse": true
            },
            "transition": "slide-horizontal"
          }
        ]
      }
    },
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "background-color": "*background:sunny-yellow",
      "fill-max-size": true
    },
    "content": {
      "items": [
        {
          "subset": "label",
          "style": "*title-label",
          "content": {
            "value": "Page-A"
          }
        },
        "*item-spacer",
        {
          "subset": "layout-linear",
          "style": {
            "orientation": "horizontal",
            "background-color": "*background:funny-color",
            "fill-max-width": true
          },
          "content": {
            "items": [
              {
                "subset": "button",
                "content": {
                  "label": {
                    "style": { "id": "*label-main-button-back" },
                    "content": {
                      "value": "BACK"
                    }
                  },
                  "action": "navigate://local-destination/back"
                }
              },
              "*item-spacer",
              {
                "subset": "button",
                "content": {
                  "label": {
                    "style": { "id": "*label-main-button-back" },
                    "content": {
                      "value": "Page-B"
                    }
                  },
                  "action": "navigate://url/page-b"
                }
              }
            ]
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