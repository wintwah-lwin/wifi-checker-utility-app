package au.edu.jcu.assessment.utilityapp.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface SpeedApi {
    @HEAD
    suspend fun pingServer(@Url url: String): Response<Void>

    @GET
    @Streaming
    suspend fun downloadPayload(
        @Url url: String,
        @Header("Cache-Control") cacheControl: String = "no-cache"
    ): Response<ResponseBody>
}
