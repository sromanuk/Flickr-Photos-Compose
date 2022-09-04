package com.example.flickrimages.data.clients.utils

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class RequestRetrier @Inject constructor(
    private val ktorClient: HttpClient,
) {

    suspend fun performRequest(request: suspend (ktorClient: HttpClient) -> HttpResponse): HttpResponse? {
        var httpResponse: HttpResponse
        var errorCounter = 0

        do {
            delay((errorCounter * TIMEOUT_STEP).seconds)

            httpResponse = try {
                request(ktorClient)
            } catch (e: ClientRequestException) {
                Log.d("RequestRetrier", "Request exception", e)
                return request(ktorClient)
            }

            errorCounter++
        } while (httpResponse.status.value != 200 && errorCounter < MAX_RETIES)

        return when (httpResponse.status.value) {
            200 -> httpResponse
            else -> null
        }
    }

    private companion object {
        const val MAX_RETIES = 5
        const val TIMEOUT_STEP = .5
    }
}