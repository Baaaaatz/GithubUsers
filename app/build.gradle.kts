plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {
        applicationId = "com.batzalcancia.githubusers"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode(1)
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
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

    buildFeatures.viewBinding = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")

    //androidx
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.2")
    implementation("androidx.activity:activity-ktx:1.2.0-beta01")
    implementation("androidx.fragment:fragment-ktx:1.3.0-beta01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.paging:paging-runtime-ktx:3.0.0-alpha07")
    implementation("androidx.recyclerview:recyclerview:1.2.0-alpha06")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // lifecycle
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")

    //hilt
    implementation("com.google.dagger:hilt-android:2.28-alpha")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    kapt("com.google.dagger:hilt-android-compiler:2.28-alpha")

    //hilt-viewmodels
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha02")

    //material
    implementation("com.google.android.material:material:1.2.1")

    //json serializer
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.7.2")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.5.0")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //Utils
    implementation("io.coil-kt:coil:0.12.0")

    //shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //room
    implementation("androidx.room:room-runtime:2.3.0-alpha03")
    implementation("androidx.room:room-ktx:2.3.0-alpha03")
    kapt("androidx.room:room-compiler:2.3.0-alpha03")

    //data store
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha02")

    //tests
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("junit:junit:4.13.1")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.7")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("androidx.room:room-testing:2.2.5")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}