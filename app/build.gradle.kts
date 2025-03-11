plugins {
    id("com.android.application") // Android Application Plugin
    id("com.google.gms.google-services") // Google Services Plugin (for Firebase & Google Sign-In)
}

android {
    namespace = "com.example.healthmonitoringapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.healthmonitoringapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // AndroidX & Material Components
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // Google Play Services (Google Sign-In & Google Fit API)
    implementation("com.google.android.gms:play-services-auth:21.3.0") // Google Sign-In
    implementation("com.google.android.gms:play-services-fitness:21.2.0") // Google Fit API

    // Firebase Authentication (Required for Google Sign-In)
    implementation("com.google.firebase:firebase-auth:23.2.0")

    // Glide for Image Loading (Fix 'Glide' Error)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Testing Dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
