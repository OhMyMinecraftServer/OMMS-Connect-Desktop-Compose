package cn.mercury9.omms.connect.desktop.data
//
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.plugins.auth.Auth
//import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
//import io.ktor.client.plugins.auth.providers.basic
//import io.ktor.client.request.forms.submitForm
//import io.ktor.client.request.get
//import io.ktor.client.statement.HttpResponse
//import io.ktor.http.parameters
//
//object HttpService {
//    init {
//        createHttpClient()
//    }
//
//    private var httpClient: HttpClient? = null
//
//    private fun createHttpClient(): HttpClient {
//        return HttpClient(CIO)
//    }
//
//    fun getHttpClient(): HttpClient {
//        if (httpClient == null) {
//            httpClient = createHttpClient()
//        }
//        return httpClient as HttpClient
//    }
//
//    fun resetHttpClient() {
//        httpClient = createHttpClient()
//    }
//
//    fun updateHttpClientWithLogin(userName: String, password: String, targetHost: String, targetRealm: String) {
//        httpClient = getHttpClient().config {
//            install(Auth) {
//                basic {
//                    credentials {
//                        BasicAuthCredentials(
//                            username = userName,
//                            password = password
//                        )
//                    }
//                    realm = targetRealm
//                    sendWithoutRequest { request ->
//                        request.url.host == targetHost
//                    }
//                }
//            }
//        }
//    }
//
//    suspend fun get(
//        targetUrl: String,
//        targetHeaders: Map<String, String>?
//    ): HttpResponse {
//        return getHttpClient().get(targetUrl) {
//            io.ktor.http.headers {
//                targetHeaders?.let {
//                    targetHeaders.forEach { (head, str) ->
//                        append(head, str)
//                    }
//                }
//            }
//        }
//    }
//
//    suspend fun submitForm(
//        targetUrl: String,
//        targetHeaders: Map<String, String>?,
//        targetFormParameters: Map<String, String>
//    ): HttpResponse {
//        return getHttpClient().submitForm(
//            url = targetUrl,
//            formParameters = parameters {
//                targetFormParameters.forEach { (key, value) ->
//                    append(key, value)
//                }
//            }
//        ) {
//            io.ktor.http.headers {
//                targetHeaders?.let {
//                    targetHeaders.forEach { (head, str) ->
//                        append(head, str)
//                    }
//                }
//            }
//        }
//    }
//
//}
