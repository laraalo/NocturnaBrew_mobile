plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.nocturnabrew_mobile"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.nocturnabrew_mobile"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"https://unjust-tamisha-undeferrably.ngrok-free.dev/\"")
            buildConfigField("String", "AppToken", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJNeUFwcCIsImlhdCI6MTc2Mzg2MDg3MH0.xIuM-v4X9b9sZu6a2oS3MAzj9iKKos3Y73xgj7BBYSg\"")
        }

        getByName("release") {
            buildConfigField("String", "BASE_URL", "\"https://api.production.com/\"")
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Coroutines para peticiones asíncronas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

}