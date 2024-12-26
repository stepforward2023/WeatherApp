package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.`SearchView$InspectionCompanion`
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        SearchCity()


    }

    private fun SearchCity() {
        val searchview=binding.searchView
        searchview.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
               return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }

        })


    }


    private fun fetchWeatherData(city_name:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(city_name, "2553a908ba48d63dd91ba765c83cacad", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                Log.d("event", "got response")
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = "$condition"
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hpa"
                    binding.condition.text = condition
                    binding.day.text =dayName(System.currentTimeMillis())
                        binding.date.text =date()
                        binding.cityName.text = "$city_name"
                    changeIMagesAccordingToWeatherCondition(condition)

                    Log.d("TAG", "onResponse: ${temperature.toString()}")
                }

            }


            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.d("event", "failure ${t.toString()}")
            }

        })
    }

    private fun changeIMagesAccordingToWeatherCondition(conditions:String) {
        when(conditions){

            "Clear","Sunny","Clear Sky"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Haze","Partly Clouds","Mist","Clouds","Foggy","OverCast"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Rain","Thunderstorm","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView.setAnimation(R.raw.sun)}
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timeStamp:Long): String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
       return sdf.format((Date()))
    }
    private fun date():String {
        val sdf=SimpleDateFormat("dd mm yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timeStamp:Long):String {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))

    }


}



//{"coord":{"lon":75.8167,"lat":26.9167},"weather":[{"id":721,"main":"Haze","description":"haze","icon":"50n"}],"base":"stations","main":{"temp":297.77,"feels_like":297.59,"temp_min":297.77,"temp_max":297.77,"pressure":1010,"humidity":50,"sea_level":1010,"grnd_level":961},"visibility":3000,"wind":{"speed":0,"deg":0},"clouds":{"all":20},"dt":1729367501,"sys":{"type":1,"id":9170,"country":"IN","sunrise":1729385937,"sunset":1729427023},"timezone":19800,"id":1269515,"name":"Jaipur","cod":200}
//



//https://api.openweathermap.org/data/2.5/weather?q=jaipur&appid=2553a908ba48d63dd91ba765c83cacad
//{"coord":{"lon":75.8167,"lat":26.9167},"weather":[{"id":721,"main":"Haze","description":"haze","icon":"50n"}],"base":"stations","main":{"temp":298.77,"feels_like":298.54,"temp_min":298.77,"temp_max":298.77,"pressure":1012,"humidity":44,"sea_level":1012,"grnd_level":963},"visibility":3000,"wind":{"speed":1.54,"deg":0},"clouds":{"all":40},"dt":1729531438,"sys":{"type":1,"id":9170,"country":"IN","sunrise":1729472373,"sunset":1729513369},"timezone":19800,"id":1269515,"name":"Jaipur","cod":200}