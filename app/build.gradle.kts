plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.jake5113.tpkaosearchapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.jake5113.tpkaosearchapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding{
        enable = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit Library
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.converter.scalars)

    // Firebase core library
    implementation(platform("com.google.firebase:firebase-bom:31.3.0"))
    // Firebase product SDK
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Kakao Login Library
    implementation(libs.v2.user) // 카카오 로그인

    // Google Login SDk
    implementation(libs.play.services.auth)

    // Naver Login SDK
    implementation(libs.oauth)

    // Google Fused Location API
    implementation(libs.play.services.location)

    // Kakao Map SDK
    implementation(files("libs/libDaumMapAndroid.jar"))
}
