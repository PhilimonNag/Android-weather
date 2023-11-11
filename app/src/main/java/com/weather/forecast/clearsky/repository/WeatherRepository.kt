package com.weather.forecast.clearsky.repository

import com.google.gson.JsonObject
import com.weather.forecast.clearsky.model.ImageResponse
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.WeatherApiService
import retrofit2.Call
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApiService: WeatherApiService) {
    suspend fun getWeatherData(city:String): WeatherModel? {
        return weatherApiService.getWeatherData(city)
    }

    suspend fun getImageData(imageText:String): ImageResponse? {
        return weatherApiService.getImageData(imageText)
    }

}