package com.elvis.carlos.google

import com.elvis.carlos.models.Framework
import com.elvis.carlos.utils.createHttpClient
import com.elvis.carlos.utils.getJSFrameworksList
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap

suspend fun search(
    query: String,
    createClient: () -> HttpClient = { createHttpClient() }
): List<Framework> {

    if (query.isBlank()) return listOf()
    var searchUrl = ""

    withContext(Dispatchers.Default) {
        searchUrl = "https://www.google.com/search?q=${URLEncoder.encode(query, "utf-8")}"
    }

    val client = createClient();
    val response = client.get<HttpResponse>(searchUrl)

    if (response.status != HttpStatusCode.OK) {
        return listOf()
    }

    val content = response.readText()
    val links = parseLinks(content)
    val frameworks: ConcurrentHashMap<String, Int> = ConcurrentHashMap();
    val requests: MutableList<Deferred<Unit>> = mutableListOf()
    for (i in 0 until links.count() - 1 step 1) {
        val request = GlobalScope.async {
            getJSFrameworksList(i, links[i], frameworks, client)
        }
        requests.add(request)
    }
    requests.awaitAll();
    client.close()
    return frameworks
        .toList()
        .sortedBy { it.second }
        .take(5)
        .map { Framework(it.first, it.second) }
}
