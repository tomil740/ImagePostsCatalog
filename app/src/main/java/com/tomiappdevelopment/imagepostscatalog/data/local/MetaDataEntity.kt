package com.tomiappdevelopment.imagepostscatalog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meta_data")
data class MetaDataEntity(
    @PrimaryKey val id: Int = 0, // Single row (singleton table)
    val lastFetchedPage: Int = 0,
    val filteredSize: Int = 0 ,
    val totalFetchedSize: Int = 0,
    val lastUpdateTime: Long = 0L
)