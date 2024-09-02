package cn.mercury9.omms.connect.desktop.client.http

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HttpService {
    private val httpClient = HttpClient()

    suspend fun get(url: String): HttpResponse =
        withContext(Dispatchers.IO) {
            httpClient.get(url)
        }
}
