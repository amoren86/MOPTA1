plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "cat.institutmarianao.myfirebaseapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "cat.institutmarianao.myfirebaseapp"
        minSdk = 30
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firestore Database
    implementation("com.google.firebase:firebase-firestore")

    // FirebaseUI Authentication
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")
}