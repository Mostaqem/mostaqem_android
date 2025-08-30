plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.baselineprofile)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.compose.compiler)
}

android { 
    namespace = "com.mostaqem"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mostaqem"
        minSdk = 26
        targetSdk = 36
        versionCode = 13
        versionName = "1.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    androidResources {
        generateLocaleConfig = true
    }
    lint {
        checkReleaseBuilds = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            proguardFiles("benchmark-rules.pro")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.coil.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.paging.compose)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coil.svg)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.material.kolor)

    // Room
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    //Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)

    // Glance
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Media 3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.hilt.android.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)

    //Android Testing
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.runner)

    "baselineProfile"(project(":baselineprofile"))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}