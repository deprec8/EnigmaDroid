/*
 * Copyright (C) 2025-2026 deprec8
 *
 * This file is part of EnigmaDroid.
 *
 * EnigmaDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EnigmaDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EnigmaDroid.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.android.build.gradle.internal.tasks.CompileArtProfileTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.mikepenz.aboutlibraries)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "io.github.deprec8.enigmadroid"
    compileSdk {
        version = release(libs.versions.android.compileSdk.get().toInt())
    }

    defaultConfig {
        applicationId = "io.github.deprec8.enigmadroid"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 19
        versionName = "1.9.2"
    }

    androidResources {
        generateLocaleConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("upload-keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    flavorDimensions += "distribution"

    productFlavors {
        create("foss") {
            dimension = "distribution"
        }

        create("play") {
            dimension = "distribution"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs["release"]
            vcsInfo.include = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "DebugProbesKt.bin"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {

    // Modules
    implementation(project(":data"))

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    // Navigation3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)

    //Libraries
    implementation(libs.mikepenz.aboutlibraries.core)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Activity
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)

    // Browser
    implementation(libs.androidx.browser)

    // Coroutines
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    // Window
    implementation(libs.androidx.window)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Savedstate
    implementation(libs.androidx.savedstate.ktx)
}

tasks.withType<CompileArtProfileTask>().configureEach {
    if (name.contains("foss")) {
        enabled = false
    }
}