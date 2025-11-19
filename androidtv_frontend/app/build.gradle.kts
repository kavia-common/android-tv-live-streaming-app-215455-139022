plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // TV app namespace
    namespace = "com.example.androidtv_frontend"

    // Ensure compile SDK supports latest TV features
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidtv_frontend"

        // Android TV generally supports SDK 21+, but some libraries need 23+ for media playback.
        // Keep minSdk 21 as requested for broader compatibility.
        minSdk = 21
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Load signing configs from environment variables without hardcoding secrets.
    signingConfigs {
        // PUBLIC_INTERFACE
        /**
         * Release signing configuration.
         * Reads keystore settings from environment variables:
         * - ANDROID_KEYSTORE_PATH
         * - ANDROID_KEYSTORE_PASSWORD
         * - ANDROID_KEY_ALIAS
         * - ANDROID_KEY_PASSWORD
         *
         * Note: These must be set by the CI or developer environment.
         * If any are missing, Gradle will still configure but assembleRelease may fail until provided.
         */
        create("release") {
            val keystorePath = System.getenv("ANDROID_KEYSTORE_PATH")
            val keystorePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("ANDROID_KEY_ALIAS")
            val keyPassword = System.getenv("ANDROID_KEY_PASSWORD")

            if (!keystorePath.isNullOrBlank()
                && !keystorePassword.isNullOrBlank()
                && !keyAlias.isNullOrBlank()
                && !keyPassword.isNullOrBlank()
            ) {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                // Leave unsigned when env vars are not present; CI/users can still assembleDebug.
                // We avoid throwing to not block configuration.
                logger.lifecycle("Release signingConfig not fully configured. Set ANDROID_KEYSTORE_PATH, ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_ALIAS, ANDROID_KEY_PASSWORD to sign release builds.")
            }
        }
    }

    buildTypes {
        debug {
            // More diagnosable debug builds
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use the placeholder signing config; if env-vars are missing, assembleRelease will produce an unsigned APK.
            signingConfig = signingConfigs.getByName("release")
        }
    }

    // Java/Kotlin toolchains aligned with AGP 8.x recommendations
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all"
        )
    }

    // Enable viewBinding for simpler UI code
    buildFeatures {
        viewBinding = true
    }

    // Packaging excludes to avoid META-INF duplicate conflicts from transitive deps
    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/NOTICE.md",
                "META-INF/DEPENDENCIES"
            )
        }
    }
}

dependencies {
    // Android TV Core
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.tvprovider:tvprovider:1.0.0")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ExoPlayer via Jetpack Media3
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.1")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Core library desugaring for Java 8+ APIs on lower API levels
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
