plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
//    id ("kotlin-kapt")
    kotlin("kapt")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.chanho.project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chanho.project"
        minSdk = 26
        targetSdk = 33
        versionCode = 2
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.google.android.gms:play-services-ads-lite:22.6.0")
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation(project(":alarm"))
    implementation(project(":calendar"))
    implementation(project(":imagerolling"))

    implementation("androidx.room:room-ktx:2.6.1")
    implementation(project(mapOf("path" to ":common")))
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation(project(mapOf("path" to ":Localization")))
    implementation(project(mapOf("path" to ":graph")))
    implementation(project(mapOf("path" to ":motion")))
    implementation(project(mapOf("path" to ":camera")))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //room
    val room_version = "2.5.0"
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    //hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    // Declare the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation ("com.google.firebase:firebase-auth")

    //firebase ui
    implementation ("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation ("androidx.work:work-runtime-ktx:2.7.0")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
    generateStubs = true
}

hilt {
    enableAggregatingTask = true
}