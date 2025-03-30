package com.tomiappdevelopment.imagepostscatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tomiappdevelopment.imagepostscatalog.presenation.PostsCatalogScreen
import com.tomiappdevelopment.imagepostscatalog.theme.PostsCatalogTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PostsCatalogTheme {

                    PostsCatalogScreen(viewModel = koinViewModel())

            }
        }
    }
}

