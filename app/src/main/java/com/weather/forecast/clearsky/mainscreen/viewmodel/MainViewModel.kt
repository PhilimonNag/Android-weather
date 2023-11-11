package com.weather.forecast.clearsky.mainscreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.weather.forecast.clearsky.model.ImageResponse
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.ResultData
import com.weather.forecast.clearsky.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val useCase: WeatherUseCase) : ViewModel() {

    fun getWeatherData(city:String): LiveData<ResultData<WeatherModel>> {
        return useCase.getWeatherData(city).asLiveData()
    }
    fun getImageData(imageText:String):LiveData<ResultData<ImageResponse>>{
        return useCase.getImageData(imageText).asLiveData()
    }
}