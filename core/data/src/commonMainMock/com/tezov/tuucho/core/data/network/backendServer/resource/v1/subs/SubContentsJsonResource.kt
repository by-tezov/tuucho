package com.tezov.tuucho.core.data.network.backendServer.resource.v1.subs

import com.tezov.tuucho.core.data.network.backendServer.resource.JsonResourceProtocol

class SubContentsJsonResource : JsonResourceProtocol {

    override val url = "subs/sub-contents"

    override val statusCode = 200

    override val jsonString = """
{
  "contents": [
    {
      "id": "input-field-age-content",
      "title": { "id": "*form:text-form-email-title" },
      "placeholder": { "id": "*form:text-form-email-placeholder" },
      "message-error": [
        {
          "id": {
            "value": "validator-1"
          },
          "default": "your email is not valid"
        },
        {
          "id": {
            "value": "validator-2"
          },
          "default": "email can't be empty"
        }
      ]
    }
  ]
}
"""
}