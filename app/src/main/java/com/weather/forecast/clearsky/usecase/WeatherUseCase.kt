package com.weather.forecast.clearsky.usecase

import android.media.Image
import com.google.gson.JsonObject
import com.weather.forecast.clearsky.model.ImageResponse
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.repository.WeatherRepository
import com.weather.forecast.clearsky.network.ResultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    fun getWeatherData(city: String): Flow<ResultData<WeatherModel>> {
        return flow {
            emit(ResultData.Loading)

            val weatherModel = weatherRepository.getWeatherData(city)

            val resultData = if (weatherModel == null) {
                ResultData.Failed()
            } else {
                ResultData.Success(weatherModel)
            }
            emit(resultData)
        }.catch {
            emit(ResultData.Failed())
        }
    }

    fun getImageData(imageText: String):Flow<ResultData<ImageResponse>>{
        return flow {
            emit(ResultData.Loading)
            val imageResponse=weatherRepository.getImageData(imageText)

            val resultData = if (imageResponse == null) {
                ResultData.Failed()
            } else {
                ResultData.Success(imageResponse)
            }
            emit(resultData)

            //emit(resultData)
        }.catch {
            emit(ResultData.Failed())
        }
    }
}