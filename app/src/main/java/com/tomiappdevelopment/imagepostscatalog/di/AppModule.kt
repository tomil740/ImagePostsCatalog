package com.tomiappdevelopment.imagepostscatalog.di

import androidx.room.Room
import com.tomiappdevelopment.imagepostscatalog.data.PostRepositoryImpl
import com.tomiappdevelopment.imagepostscatalog.data.local.PostsDb
import com.tomiappdevelopment.imagepostscatalog.data.remote.PostApiService
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import com.tomiappdevelopment.imagepostscatalog.presenation.PostsCatalogViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single {
        Room.databaseBuilder(get(), PostsDb::class.java, "posts_db")
            .fallbackToDestructiveMigration() // Ensure Room can migrate the database if needed
            .build()
    }

    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/") // Make sure this is correct!
            .client(get())
            .addConverterFactory(GsonConverterFactory.create()) // Gson for JSON parsing
            .build()
    }

    single<PostApiService> {
        get<Retrofit>().create(PostApiService::class.java)
    }

    single { get<PostsDb>().postDao() }

    single<PostRepository> { PostRepositoryImpl(get(),get()) }


    viewModel { PostsCatalogViewModel(get()
    ) }

}