package com.elvis.carlos.middlewares

import com.elvis.carlos.data.GoogleDataSource
import com.elvis.carlos.data.IDataSource
import com.elvis.carlos.models.Framework
import com.elvis.carlos.models.SearchResponse
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

private suspend fun search(query: String, dataSource: IDataSource = GoogleDataSource()): List<Framework> {
    if (query.isBlank()) return listOf()

    val frameworks: ConcurrentHashMap<String, Int> = ConcurrentHashMap()
    val sitesLinksList = dataSource.getSitesList(query)
    val requests: MutableList<Deferred<Unit>> = mutableListOf()

    for (i in 0 until sitesLinksList.count() - 1 step 1) {
        val request = GlobalScope.async {
            val siteFrameworks = dataSource.getSiteFrameworks(sitesLinksList[i])
            siteFrameworks.forEach {
                frameworks[it] = frameworks.getOrDefault(it, 0).inc()
            }
        }
        requests.add(request)
    }

    requests.awaitAll();

    return frameworks
        .toList()
        .sortedBy { it.second }
        .take(5)
        .map { Framework(it.first, it.second) }
}

suspend fun PipelineContext<Unit, ApplicationCall>.indexPage() {
    val query = call.request.queryParameters["search"]?.trim() ?: ""
    val frameworks = try {
        search(query)
    } catch (error: Exception) {
        println(error.message)
        listOf<Framework>()
    }
    call.respond(MustacheContent("index.hbs", mapOf("data" to SearchResponse(query, frameworks))))
}