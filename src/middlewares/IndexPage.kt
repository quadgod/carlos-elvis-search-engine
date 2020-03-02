package com.elvis.carlos.middlewares

import com.elvis.carlos.google.search
import com.elvis.carlos.models.Framework
import com.elvis.carlos.models.SearchResponse
import io.ktor.application.ApplicationCall
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import java.lang.Exception

suspend fun ApplicationCall.indexPage() {
    val query = request.queryParameters["search"]?.trim() ?: ""
    val frameworks = try { search(query) } catch (error: Exception) {
        println(error.message)
        listOf<Framework>()
    }
    response.call.respond(MustacheContent("index.hbs", mapOf("data" to SearchResponse(query, frameworks))))
}