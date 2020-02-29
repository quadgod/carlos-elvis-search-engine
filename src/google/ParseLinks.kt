package com.elvis.carlos.google

fun parseLinks(input: String): List<String> {
    val linksSearchRegex = Regex("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1")
    return linksSearchRegex
        .findAll(input)
        .map { it.groupValues.last() }
        .filter { it.startsWith("/url?q=") }
        .map { it.substring(7) }
        .toList()
}