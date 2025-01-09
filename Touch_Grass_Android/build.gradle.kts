plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.tracking.touchgrass.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.tracking.touchgrass.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    //implementation(libs.androidx.material3.android)



    debugImplementation(libs.compose.ui.tooling)



    // CameraX
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
    implementation ("androidx.camera:camera-camera2:1.4.0")
    // Google ML Kit for image labeling
    implementation (libs.image.labeling)

    // Coroutines for state management
    implementation (libs.kotlinx.coroutines.android)

    // AndroidX ViewModel with Compose support
    implementation (libs.androidx.lifecycle.viewmodel.compose)

    implementation (libs.accompanist.permissions)
    implementation (libs.androidx.navigation.compose)



}