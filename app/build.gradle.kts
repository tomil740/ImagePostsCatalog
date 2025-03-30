import com.android.manifmerger.Actions.load
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.devtools.ksp")
}
val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

val pixabayApiKey: String = localProperties["api_key"] as String

android {
    namespace = "com.tomiappdevelopment.imagepostscatalog"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tomiappdevelopment.imagepostscatalog"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"$pixabayApiKey\"")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        buildTypes {
            debug {
                buildConfigField("String", "API_KEY", "\"your-debug-api-key\"")
            }
            release {
                buildConfigField("String", "API_KEY", "\"your-release-api-key\"")
            }
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {


    // AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)

    // Compose Libraries
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.coil.compose)

    // Koin Libraries
    implementation(libs.bundles.koin.compose)
    implementation(libs.bundles.koin)

    // Room Database
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler) // Use kapt for room-compiler!


    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug Libraries
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}