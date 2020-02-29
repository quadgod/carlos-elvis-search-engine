package com.elvis.carlos.google

import com.elvis.carlos.models.SearchResultItem
import com.elvis.carlos.utils.createHttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

suspend fun search(query: String): Array<SearchResultItem> {
    if (query.isBlank()) return arrayOf()

    var searchUrl = ""

    withContext(Dispatchers.Default) {
        searchUrl = "https://www.google.com/search?q=${URLEncoder.encode(query, "utf-8")}"
    }

    val client = createHttpClient();
    val response = client.get<HttpResponse>(searchUrl)

    if (response.status != HttpStatusCode.OK) {
        return arrayOf()
    }

    val content = response.readText()
    println(content);

    client.close()

    return arrayOf()
}
