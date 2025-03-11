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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }
}

dependencies {
    // AndroidX & UI Components
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // Google Play Services (Google Sign-In & Google Fit API)
    implementation("com.google.android.gms:play-services-auth:21.1.1") // Google Sign-In
    implementation("com.google.android.gms:play-services-fitness:21.1.0") // Google Fit API

    // Firebase Authentication (Required for Google Sign-In)
    implementation("com.google.firebase:firebase-auth:22.1.1")

    // Firebase Core (Ensure Firebase initialization)
    implementation("com.google.firebase:firebase-core:21.1.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
