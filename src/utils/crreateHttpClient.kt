package com.elvis.carlos.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

fun createHttpClient(): HttpClient {
    return HttpClient(engineFactory = CIO) {
        engine {
            maxConnectionsCount = 3
        }
    }
}