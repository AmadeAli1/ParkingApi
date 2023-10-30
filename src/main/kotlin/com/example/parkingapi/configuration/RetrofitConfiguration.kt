package com.example.parkingapi.configuration

import com.example.parkingapi.service.mpesa.MpesaUtils
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Configuration
class RetrofitConfiguration {

    @Bean("c2b")
    @Lazy
    fun providesC2BRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .readTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(40, TimeUnit.SECONDS)
                    .build()
            )
            .baseUrl(MpesaUtils.Mpesa_C2B_Base_Url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}