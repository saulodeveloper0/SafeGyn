package com.saulodev.safegyn

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: LocationEntity)

    @Query("SELECT * FROM locations")
    suspend fun getAllLocations(): List<LocationEntity>
}