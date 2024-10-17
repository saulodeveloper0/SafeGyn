package com.saulodev.safegyn

import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Modelo de dados para o Retrofit
data class LocationData(
    val latitude: Double,
    val longitude: Double
)

// Interface da API
interface LocationApi {
    @POST("/locations")  // Endpoint da API para salvar a localização
    fun saveLocation(@Body location: LocationData): Call<LocationData>
}

// Função para criar a instância do Retrofit
fun createRetrofit(): LocationApi {
    val retrofit = Retrofit.Builder()
        .baseUrl("localhost:3000") // Substitua pelo IP do servidor
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(LocationApi::class.java)
}

// Função para salvar a localização
fun saveLocation(context: Context, latitude: Double, longitude: Double) {
    val locationApi = createRetrofit()  // Criar instância do Retrofit
    val location = LocationData(latitude = latitude, longitude = longitude)

    locationApi.saveLocation(location).enqueue(object : retrofit2.Callback<LocationData> {
        override fun onResponse(call: Call<LocationData>, response: retrofit2.Response<LocationData>) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Localização salva!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Falha ao salvar localização", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<LocationData>, t: Throwable) {
            Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}
