package com.example.listycity

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore

class CityRepository {

    private val citiesRef = FirebaseFirestore.getInstance().collection("cities")
    private val _cities = mutableStateListOf<City>()

    init {
        citiesRef.addSnapshotListener { value, error ->
            // 1. if there's an error, log it and stop
            if (error != null) {
                Log.e("CityRepository", "Error fetching cities", error)
                return@addSnapshotListener
            }
            // 2. if value isn't null:
            //      clear _cities
            //      loop over value, convert each doc to a City, add it to _cities

            _cities.clear()
            value?.documents?.forEach {
                val city = it.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }
    }

    val cities: List<City>
        get() = _cities

    fun addCity(city: City) {
        citiesRef.document(city.name).set(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {

        citiesRef.document(oldCity.name).delete() // delete the city
        citiesRef.document(updatedCity.name).set(updatedCity) // add the updated city
    }

    fun deleteCity(city: City) {
        citiesRef.document(city.name).delete()
    }

}