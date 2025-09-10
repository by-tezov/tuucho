package com.tezov.tuucho.core.data.network.backendServer.resource.v1

import com.tezov.tuucho.core.data.network.backendServer.resource.JsonResourceProtocol

class PageHomeContextualJsonResource : JsonResourceProtocol {

    override val url = "page-home-contextual"

    override val statusCode = 200

    override val jsonString = """
{
  "version": "1",
  "components": [
    {
      "id": "input-field-on-demand",
      "subset": "field",
      "option": {
        "form-validator": "string-not-null"
      },
      "content": {
        "id": "input-field-age-content",
        "title": "hbbies",
        "placeholder": "cook dead body parts",
        "message-error": "can't be null, i'm sure you have secret"
      }
    }
  ],
  "texts": {
    "common": {
      "fake-value-content": "on-demand-text"
    }
  }
}
"""
}