plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.aaos.music.core.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 30
    }

    buildFeatures {
        compose = true
    }


    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    
    testImplementation(libs.junit)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    implementation(libs.coil.compose)
}
