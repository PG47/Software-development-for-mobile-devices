plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.gallery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gallery"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(project(":OpenCC"))
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bfrachia:android-image-cropper:2.7.0")

    implementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("androidx.credentials:credentials:1.3.0-alpha02")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")

    implementation ("com.github.chrisbanes:PhotoView:2.0.0")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
}