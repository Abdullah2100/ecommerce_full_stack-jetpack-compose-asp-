import org.gradle.kotlin.dsl.implementation
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp.android)
    alias(libs.plugins.kotlin.serialize.plugin)
    alias(libs.plugins.google.gms)
}

android {
    namespace = "com.example.e_commercompose"
    compileSdk = 36
    ndkVersion = "28.2.13676358"

    defaultConfig {
        applicationId = "com.example.e_commercompose"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
        val mapboxToken = localProperties.getProperty("GOOGLE_MAP_KEY") ?: ""
        resValue("string", "google_map_token", mapboxToken)
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

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildToolsVersion = "36.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.ui.graphics)
    implementation(libs.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //material icon
    implementation(libs.androidx.material.icons.extended)


    //koin
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.nav)
    implementation(libs.koin.compose)

    // Ktor dependencies
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.loggin)
    implementation(libs.ktor.auth)
    implementation(libs.ktor.cio)

    // Kotlinx Serialization JSON
    implementation(libs.kotlinx.serialization.json)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //navigation
    implementation(libs.compose.navigation)

    //constrain
    implementation(libs.compose.constrin)


    //room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)

    //secureDataBase
    implementation(libs.sqlcipher)
    implementation(libs.sql.light)

    //splashScreen
    implementation(libs.androidx.core.splashscreen)

    //desugar
    coreLibraryDesugaring(libs.desugar.jdk.libs)


    //location
    implementation(libs.play.services.location)

    //coroutine task
    implementation(libs.kotlinx.coroutines.play.services)


    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    //signalR
    implementation(libs.signalr)

    //firebase
    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.messaging.directboot)


    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.compose.runntime)

    //qc
   // implementation(libs.qr.kit)
    implementation(libs.zxing.android.embedded)
    implementation(libs.core)

    //live time
    implementation (libs.androidx.runtime.livedata)

    //google map
    implementation(libs.maps.utils.ktx)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    //local datetime kotlin
    implementation(libs.kotlinx.datetime)



}

configurations.all {
    resolutionStrategy.force(libs.androidx.junit)
    resolutionStrategy.force(libs.androidx.espresso.core)
}