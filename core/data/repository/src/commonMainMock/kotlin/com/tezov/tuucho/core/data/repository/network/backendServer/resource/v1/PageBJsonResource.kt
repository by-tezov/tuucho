package com.tezov.tuucho.core.data.repository.network.backendServer.resource.v1

import com.tezov.tuucho.core.data.repository.network.backendServer.resource.JsonResourceProtocol

class PageBJsonResource : JsonResourceProtocol {

    override val url = "page-b"

    override val statusCode = 200

    override val jsonString = """
{
  "root": {
    "setting": {
      "navigation": {
        "definition": [
          {
            "option": {
              "single": true
            },
            "transition": {
              "type": "slide-horizontal",
              "entrance": "from-start",
              "effect": "push"
            }
          }
        ]
      }
    },
    "subset": "layout-linear",
    "style": {
      "orientation": "vertical",
      "background-color": "*background:crazy-orange",
      "fill-max-size": true
    },
    "content": {
      "items": [
        {
          "subset": "label",
          "style": "*title-label",
          "content": {
            "value": "Page-B"
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
                    "style": "*label-main-button-default",
                    "content": {
                      "value": "BACK"
                    }
                  },
                  "action": "navigate://local-destination/back"
                }
              },
              {
                "subset": "button",
                "content": {
                  "label": {
                    "style": { "id": "*label-main-button-back" },
                    "content": {
                      "value": "Another Page-A"
                    }
                  },
                  "action": "navigate://url/page-a"
                }
              },
              "*item-spacer",
              {
                "subset": "button",
                "content": {
                  "label": {
                    "style": "*label-main-button-default",
                    "content": {
                      "value": "Page-C"
                    }
                  },
                  "action": "navigate://url/page-c"
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