import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    val envFile = rootProject.file("env.properties")
    val env = Properties()

    if (envFile.exists()) {
        env.load(FileInputStream(envFile))
    }

    namespace = "com.example.rfid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.rfid"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug{
            buildConfigField(
                "boolean",
                "USE_MOCK_AUTH",
                (env["USE_MOCK_AUTH"] ?: "false").toString())
            buildConfigField(
                "String",
                "BASE_URL",
                "\"${env["API_BASE_URL"] ?: "http://10.0.2.2:8080/api"}\""
            )
        }
        release {
            buildConfigField("boolean", "USE_MOCK_AUTH", "false")
            buildConfigField(
                "String",
                "BASE_URL",
                "\"${env["API_BASE_URL"] ?: "http://10.0.2.2:8080/api"}\""
            )
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation("com.auth0.android:jwtdecode:2.0.2")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-jackson:3.0.0")
    implementation(libs.okhttp)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.viewpager2:viewpager2:1.0.0")
}