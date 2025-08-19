package com.tezov.tuucho.core.data.network.backendServer.resource.v1.subs

import com.tezov.tuucho.core.data.network.backendServer.resource.JsonResourceProtocol

class SubTextsJsonResource : JsonResourceProtocol {

    override val url = "subs/sub-texts"

    override val statusCode = 200

    override val jsonString = """
{
  "version": "1",
  "texts": {
    "common": {
      "text-body-content-help": "There is no help available at the moment"
    }
  }
}
"""
}