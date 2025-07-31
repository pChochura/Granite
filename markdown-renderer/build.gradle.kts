import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinComposeCompiler)
}

android {
    compileSdk = libs.versions.targetSdk.get().toInt()
    namespace = "${libs.versions.packageName.get()}.markdown.renderer"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(JavaVersion.VERSION_17.toString())
        }
    }
}

dependencies {
    implementation(platform(libs.composeBom))
    implementation(libs.composeUi)

    implementation(libs.markdown)
    implementation(libs.highlights)
    implementation(projects.markdownObsidianParser)
}
