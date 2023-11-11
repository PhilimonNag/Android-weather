package com.weather.forecast.clearsky.network

import com.google.gson.JsonObject
import com.weather.forecast.clearsky.model.ImageResponse
import com.weather.forecast.clearsky.model.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather/{q}")
    suspend fun getWeatherData(
        @Path("q") city: String
    ): WeatherModel?

    @GET("image/{image}")
    suspend fun getImageData(@Path("image")imageText:String):ImageResponse?
}