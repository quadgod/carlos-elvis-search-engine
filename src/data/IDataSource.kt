package com.elvis.carlos.data

interface IDataSource {
    suspend fun getSitesList(query: String): List<String>
    suspend fun getSiteFrameworks(url: String): List<String>
}