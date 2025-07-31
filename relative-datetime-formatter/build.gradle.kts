import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
}

android {
    compileSdk = libs.versions.targetSdk.get().toInt()
    namespace = "${libs.versions.packageName.get()}.relative.datetime.formatter"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(JavaVersion.VERSION_17.toString())
        }
    }
}
