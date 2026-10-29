package au.edu.jcu.assessment.utilityapp.data.remote

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

/** Cloudflare's public speed-test endpoints. Base URL: https://speed.cloudflare.com/ */
interface SpeedApi {

    /** Streams [bytes] of generated data. Use bytes = 0 for a near-empty latency probe. */
    @Streaming
    @GET("__down")
    suspend fun download(@Query("bytes") bytes: Long): ResponseBody

    /** Accepts an upload body and discards it. */
    @POST("__up")
    suspend fun upload(@Body body: RequestBody): ResponseBody
}