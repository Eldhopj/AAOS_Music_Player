@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
}

// Global Configuration for Subprojects (Jacoco)
subprojects {
    apply(plugin = "jacoco")

    configure<org.gradle.testing.jacoco.plugins.JacocoPluginExtension> {
        toolVersion = "0.8.12"
    }

    // Register Jacoco Report Task
    tasks.register<JacocoReport>("jacocoTestReport") {
        dependsOn("testDebugUnitTest")
        group = "Reporting"
        description = "Generate Jacoco coverage reports"

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }

        val fileFilter = listOf(
            "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*",
            "android/**/*.*", "**/*\$ViewInjector*.*", "**/*\$ViewBinder*.*",
            "**/databinding/*", "**/android/databinding/*", "**/androidx/databinding/*",
            "**/di/module/*", "**/*MapperImpl*.*", "**/*\$Lambda$*.*", "**/*Companion*.*",
            "**/*Module.*", "**/*Dagger*.*", "**/*Hilt*.*", "**/*MembersInjector*.*",
            "**/*_Factory*.*", "**/*_Provide*Factory*.*", "**/*Extensions*.*",
            "**/*\$Result.*", "**/*\$Result$*.*"
        )

        val buildLayout = layout.buildDirectory.get().asFile
        
        val debugTree = fileTree("$buildLayout/tmp/kotlin-classes/debug") {
            exclude(fileFilter)
        }
        val javaTree = fileTree("$buildLayout/intermediates/javac/debug/classes") {
            exclude(fileFilter)
        }
        
        val mainSrc = "${project.projectDir}/src/main/java"
        val kotlinSrc = "${project.projectDir}/src/main/kotlin"

        sourceDirectories.setFrom(files(mainSrc, kotlinSrc))
        classDirectories.setFrom(files(debugTree, javaTree))
        executionData.setFrom(fileTree(buildLayout) {
            include("**/*.exec", "**/*.ec")
        })
    }

    // Configure Test tasks with JVM arguments for Java 17+ support
    tasks.withType<Test> {
        jvmArgs(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED"
        )
    }

    // Configure Android Extensions safely using type-specific blocks
    plugins.withId("com.android.application") {
        extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
            buildTypes.getByName("debug") {
                enableAndroidTestCoverage = true
                enableUnitTestCoverage = true
            }
            testOptions {
                unitTests.isIncludeAndroidResources = true
                unitTests.isReturnDefaultValues = true
            }
        }
    }

    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.api.dsl.LibraryExtension> {
            buildTypes.getByName("debug") {
                enableAndroidTestCoverage = true
                enableUnitTestCoverage = true
            }
            testOptions {
                unitTests.isIncludeAndroidResources = true
                unitTests.isReturnDefaultValues = true
            }
        }
    }
}


true // Needed to make the script return a value
