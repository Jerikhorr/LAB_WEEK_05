package com.example.lab_week_05

import android.os.Bundle
import android.util.Log // For Log
import android.widget.TextView // For TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_week_05.api.CatApiService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab_week_05.model.ImageData
import retrofit2.Call // For Retrofit Call
import retrofit2.Callback // For Retrofit Callback
import retrofit2.Response // For Retrofit Response
import retrofit2.Retrofit // For Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val catApiService by lazy{
        retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy{
        findViewById(R.id.api_response)
    }

    private val imageResultView: ImageView by lazy {
        findViewById(R.id.image_result)
    }

    private val imageLoader: ImageLoader by lazy {
        GlideLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getCatImageResponse()
    }
    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object: Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)

            }
            override fun onResponse(call: Call<List<ImageData>>,
                                    response: Response<List<ImageData>>) {
                if(response.isSuccessful){
                    val image = response.body()
                    val firstImage = image?.firstOrNull()
                    val firstImageBreed = firstImage?.breeds?.firstOrNull()?.name.orEmpty()
                    if (firstImage != null) {
                        imageLoader.loadImage(firstImage.imageUrl, imageResultView)
                        apiResponseView.text = if (firstImageBreed.isNotBlank()) "Cat Breed : " + firstImageBreed else " Cat Breed : Unknown"
                    } else {
                        Log.d(MAIN_ACTIVITY, "Missing image URL")
                        apiResponseView.text = "Unknown"
                    }


                }
                else{
                    Log.e(MAIN_ACTIVITY, "Failed to get response\n" +
                            response.errorBody()?.string().orEmpty()
                    )
                }
            }
        })
    }
    companion object{
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }



}