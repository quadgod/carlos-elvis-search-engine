package com.elvis.carlos.utils

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import io.ktor.util.KtorExperimentalAPI
import java.util.concurrent.ConcurrentHashMap

@KtorExperimentalAPI
suspend fun getJSFrameworksList(
    i: Int,
    url: String,
    frameworks: ConcurrentHashMap<String, Int>,
    httpClient: HttpClient
) {
    println("$i $url") // Did this print to demonstrate that it's works in parallel mode

    val response = httpClient.get<HttpResponse>(url)
    if (response.status != HttpStatusCode.OK) {
        return
    }

    val content = response.readText()
    if (content.contains("data-reactroot") || content.contains("data-reactid")) {
        frameworks["react"] = frameworks.getOrDefault("react", 0).inc()
    }

    if (content.contains(".ng-binding") ||
        content.contains("[ng-app]") ||
        content.contains("[data-ng-app]") ||
        content.contains("[ng-controller]") ||
        content.contains("[data-ng-controller]") ||
        content.contains("[ng-repeat]") ||
        content.contains("[data-ng-repeat]")
    ) {
        frameworks["angular"] = frameworks.getOrDefault("angular", 0).inc()
    }

    if (content.contains("emberjs") || content.contains("id=\"ember")) {
        frameworks["emberjs"] = frameworks.getOrDefault("ember", 0).inc()
    }

    if (content.contains("jquery.min.js")) {
        frameworks["jquery"] = frameworks.getOrDefault("jquery", 0).inc()
    }

    if (content.contains("bootstrap.min.js")) {
        frameworks["bootstrap"] = frameworks.getOrDefault("bootstrap", 0).inc()
    }
}