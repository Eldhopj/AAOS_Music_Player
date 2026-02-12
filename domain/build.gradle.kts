plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.aaos.music.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 30
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
}
