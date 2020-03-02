package com.elvis.carlos.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

const val USER_AGENT = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"

class GoogleDataSource : IDataSource {
    private fun parseLinks(html: String): List<String> {
        val linksSearchRegex = Regex("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1")
        return linksSearchRegex
            .findAll(html)
            .map { it.groupValues.last() }
            .filter { it.startsWith("/url?q=") }
            .map { it.substring(7) }
            .toList()
    }

    override suspend fun getSitesList(query: String): List<String> {
        if (query.isBlank()) return listOf()
        val url = "https://www.google.com/search?q=${URLEncoder.encode(query, "utf-8")}"
        val html = withContext(Dispatchers.IO) {
            val address = URL(url)
            with(address.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                addRequestProperty("User-Agent",USER_AGENT)
                inputStream.bufferedReader().readText()
            }
        }
        return parseLinks(html);
    }

    override suspend fun getSiteFrameworks(url: String): List<String> {
        val html = withContext(Dispatchers.IO) {
            val address = URL(url)
            with(address.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                addRequestProperty("User-Agent", USER_AGENT)
                inputStream.bufferedReader().readText()
            }
        }

        val frameworks = mutableListOf<String>()

        if (html.contains("data-reactroot") || html.contains("data-reactid")) {
            frameworks.add("react")
        }

        if (html.contains("ng-binding") ||
            html.contains("[ng-app]") ||
            html.contains("[data-ng-app]") ||
            html.contains("[ng-controller]") ||
            html.contains("[data-ng-controller]") ||
            html.contains("[ng-repeat]") ||
            html.contains("[data-ng-repeat]")
        ) {
            frameworks.add("angular")
        }

        if (html.contains("emberjs") || html.contains("id=\"ember")) {
            frameworks.add("emberjs")
        }

        if (html.contains("jquery.min.js")) {
            frameworks.add("jquery")
        }

        if (html.contains("bootstrap.min.js")) {
            frameworks.add("bootstrap")
        }

        return frameworks.toList()
    }
}