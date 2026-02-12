plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.spotless) apply false
}

android {
    namespace = "com.example.aaos.music"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aaos.music"
        minSdk = 30 // Android Automotive standard usually
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }


    buildTypes {
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":service-media"))
    implementation(project(":data"))
    implementation(project(":domain"))
    
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.foundation)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.datastore.preferences)
    ksp(libs.hilt.compiler)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    kspTest(libs.hilt.compiler)
    testImplementation(platform(libs.compose.bom))
    debugImplementation(platform(libs.compose.bom))
    testImplementation(libs.material3)
    testImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.robolectric)

    // Android Test dependencies
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    testImplementation(platform(libs.compose.bom))
    debugImplementation(platform(libs.compose.bom))
    testImplementation(libs.material3)
// Unit tests on JVM using Robolectric
    testImplementation(libs.junit)
    testImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.robolectric)


// Roborazzi (core + JUnit rule helpers)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)

// Jacoco configuration for code coverage
    tasks.register<JacocoReport>("jacocoTestReport") {
        dependsOn("testDebugUnitTest")

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }

        val fileFilter = listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/*\$ViewInjector*.*",
            "**/*\$ViewBinder*.*",
            "**/databinding/*",
            "**/android/databinding/*",
            "**/androidx/databinding/*",
            "**/di/module/*",
            "**/*MapperImpl*.*",
            "**/*\$Lambda$*.*",
            "**/*Companion*.*",
            "**/*Module.*",
            "**/*Dagger*.*",
            "**/*Hilt*.*",
            "**/*MembersInjector*.*",
            "**/*_Factory*.*",
            "**/*_Provide*Factory*.*",
            "**/*Extensions*.*",
            "**/*\$Result.*",
            "**/*\$Result$*.*"
        )

        val debugTree =
            fileTree("${project.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
                exclude(fileFilter)
            }

        val mainSrc = "${project.projectDir}/src/main/java"

        sourceDirectories.setFrom(files(mainSrc))
        classDirectories.setFrom(files(debugTree))
        executionData.setFrom(fileTree(project.layout.buildDirectory.get().asFile) {
            include("**/*.exec", "**/*.ec")
        })
    }

}
