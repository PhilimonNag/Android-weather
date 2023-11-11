package com.weather.forecast.clearsky.mainscreen.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.mainscreen.viewmodel.MainViewModel
import com.weather.forecast.clearsky.model.ImageResponse
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.ResultData
import com.weather.forecast.clearsky.ui.theme.ClearSkyTheme
import com.weather.forecast.clearsky.ui.theme.Purple40
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ClearSkyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(8.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center){
                        var locationInput by remember { mutableStateOf("Amsterdam") }

                        // Collect the data using observeAsState within the composable
                        val weatherDataResult by viewModel.getWeatherData(locationInput).observeAsState()
                        val imageResultData by viewModel.getImageData("Patchy rain possible").observeAsState()
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                           Column(Modifier.padding(24.dp),
                               verticalArrangement = Arrangement.Top,
                               horizontalAlignment = Alignment.CenterHorizontally
                           ) {
                               SearchBar { newLocationInput ->
                                   if (locationInput != newLocationInput) {
                                       locationInput = newLocationInput
                                       viewModel.getWeatherData(newLocationInput)
                                   }
                               }
                               Spacer(modifier = Modifier.height(50.dp))
                               when (weatherDataResult) {
                                   is ResultData.Success -> {
                                       (weatherDataResult as ResultData.Success<WeatherModel>).data?.let { weatherData ->
                                           WeatherView(weatherData = weatherData)
                                           viewModel.getImageData("Generate an image representing the weather condition: "+weatherData.current.condition.text)
//                                           if(imageResultData!=null){
//                                               customImageView(imageUrl = imageResultData.)
//                                           }
                                       }
                                   }
                                   is ResultData.Failed -> {
                                       CustomText( (weatherDataResult as ResultData.Failed).message.toString())
                                   }
                                   is ResultData.Loading -> {
                                       AnimationLoading()
                                   }
                                   null -> {
                                       CustomText(name = "Enter Valid City Name")
                                   }
                               }

                               when(imageResultData){
                                   is ResultData.Success-> {
                                       (imageResultData as ResultData.Success<ImageResponse>).data?.let {
                                           customImageView(imageUrl = it.data)
                                       }
                                   }

                                   else -> {
//                                      customImageView(imageUrl = weatherDataResult.)
                                   }
                               }



                           }
                        }
                    }
                }
            }
        }
      }
    }



@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var locationText by remember { mutableStateOf(TextFieldValue()) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomTextField(value = locationText) {
            locationText = it
        }
        IconButton(
            onClick = {
                // Pass the text to the callback function
                onSearch(locationText.text)
            },
            modifier = Modifier.background(Color(0xFF6650a4), shape = RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "search button",
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        Modifier.background(color = Color.Transparent, shape = RoundedCornerShape(30.dp)),
        placeholder = { Text(text = "Enter Location") }
    )
}


@Composable
fun WeatherView(weatherData:WeatherModel){

        Column {

            TwoTextRow(text1 = "Location : ", text2 = weatherData?.location?.name.toString())
            TwoTextRow(text1 = "Country  : ", text2 = weatherData?.location?.country.toString())
            TwoTextRow(text1 = "Temp     : ", text2 = "${weatherData?.current?.temp_c}℃")
            TwoTextRow(text1 = "Condition :", text2 = weatherData?.current?.condition?.text.toString())

        }


    //"https://cdn.pixabay.com/photo/2020/02/20/08/42/heavy-rain-4864257_1280.jpg"
}

@Composable
fun TwoTextRow(text1:String,text2: String){
    Row (Modifier.padding(12.dp)){
        Text(
            text = text1,
            fontSize = 16.sp)
        CustomText(name = text2)

    }
}

@Composable
fun AnimationLoading(){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim1))
    LottieAnimation(
        modifier = Modifier.size(70.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}

@Composable
fun CustomText(name: String,modifier: Modifier=Modifier){
    Text(text = name,
        modifier=modifier,
        fontSize = 18.sp,
        fontWeight = FontWeight.W500,
        fontFamily = FontFamily.Serif)
}
@Composable
fun customImageView(imageUrl:String){
    SubcomposeAsyncImage(
        model = imageUrl,
        loading = {
           AnimationLoading()
        },
        contentDescription = "Loading Image",
        modifier = Modifier
//            .padding(24.dp)
            .height(320.dp)
            .width(420.dp)


    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClearSkyTheme {
        Column (modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            //SearchBar()
            Column (
                Modifier
                    .padding(12.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ){
                CustomText(name ="Location: Amsterdam")
                CustomText(name ="Country : Netherlands ")
                CustomText(name ="Temperature : 70℃")
            }

//            customImageView(imageUrl = "https://cdn.pixabay.com/photo/2020/02/20/08/42/heavy-rain-4864257_1280.jpg")
        }
    }
}