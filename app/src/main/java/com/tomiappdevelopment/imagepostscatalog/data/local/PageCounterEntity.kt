package com.tomiappdevelopment.imagepostscatalog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page_counter")
data class PageCounterEntity(
    @PrimaryKey
    val id : Int = 0,
    val fetchedPages:Int = 0
)