plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "br.com.gestos.acessibilidade"
    compileSdk = 35
    defaultConfig {
        applicationId = "br.com.gestos.acessibilidade"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }
}
