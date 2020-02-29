package com.elvis.carlos.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun createHttpClient(): HttpClient {
    return HttpClient(engineFactory = CIO)
}