package com.elvis.carlos.models

data class SearchResponse(val search: String? = null, val data: Array<WebsiteResource> = arrayOf())
