import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.androidVersionGit)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinComposeCompiler)
    id(libs.versions.parcelizePluginName.get())
}

androidGitVersion {
    format = "%tag%%-commit%%-dirty%"
    codeFormat = "MMNNPPBBB"
}

android {
    namespace = libs.versions.packageName.get()
    compileSdk = libs.versions.targetSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.packageName.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = androidGitVersion.code().takeIf { it > 0 } ?: 1
        versionName = androidGitVersion.name().takeIf { it.isNotEmpty() } ?: "1.0"
    }

    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                rootProject.file("keystore.properties").inputStream().use(::load)
            }

            storeFile = file(properties.getProperty("storeFile"))
            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    applicationVariants.all {
        outputs.all {
            val nameBuilder = StringBuilder()
            nameBuilder.append(applicationId)
            nameBuilder.append("_$name")
            nameBuilder.append("_$versionName")
            nameBuilder.append("_$versionCode")
            nameBuilder.append(".apk")

            (this as BaseVariantOutputImpl).outputFileName = nameBuilder.toString()
        }
    }
}

dependencies {
    implementation(libs.androidxCore)
    implementation(libs.androidxNavigationCompose)
    implementation(libs.androidxSplashscreen)

    implementation(platform(libs.composeBom))
    implementation(libs.composeActivity)
    implementation(libs.composeViewModel)
    implementation(libs.composeMaterial)
    implementation(libs.composeUi)
    implementation(libs.composeUiToolingPreview)

    debugImplementation(libs.composeUiTooling)

    implementation(libs.koinCompose)
    implementation(libs.kotlinSerializationJson)

    implementation(projects.domain)
    implementation(projects.supabaseDatasource)
    implementation(projects.uiComponents)
    implementation(projects.markdownRenderer)
}
