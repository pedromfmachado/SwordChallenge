import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pedromfmachado.sword.catz"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.pedromfmachado.sword"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Load API key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val catApiKey = localProperties.getProperty("CAT_API_KEY") ?: ""
        buildConfigField("String", "CAT_API_KEY", "\"$catApiKey\"")

        testInstrumentationRunner = "com.pedromfmachado.sword.catz.HiltTestRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    // Feature modules
    implementation(project(":cat_breeds"))
    implementation(project(":cat_breeds_api"))
    implementation(project(":cat_breeds_data"))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.androidx.room.runtime)
    kspAndroidTest(libs.hilt.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coil (Image Loading)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Moshi
    implementation(libs.moshi)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
}
