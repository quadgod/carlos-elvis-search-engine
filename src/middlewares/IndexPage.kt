package com.elvis.carlos.middlewares

import com.elvis.carlos.google.search
import com.elvis.carlos.models.SearchResponse
import io.ktor.application.ApplicationCall
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond

suspend fun ApplicationCall.indexPage() {
    val query = request.queryParameters["search"]?.trim() ?: ""
    search(query)
    println(query)
    response.call.respond(MustacheContent("index.hbs", mapOf("data" to SearchResponse(query))))
}