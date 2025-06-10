plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
}

android {
    compileSdk = libs.versions.targetSdk.get().toInt()
    namespace = "${libs.versions.packageName.get()}.local.datasource"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.koinCore)

    ksp(libs.roomDbCompiler)
    annotationProcessor(libs.roomDbCompiler)
    implementation(libs.roomDb)
    implementation(libs.roomDbExtensions)

    implementation(libs.kotlinSerializationJson)
}
