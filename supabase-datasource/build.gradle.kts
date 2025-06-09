plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
}

android {
    compileSdk = libs.versions.targetSdk.get().toInt()
    namespace = "${libs.versions.packageName.get()}.supabase.datasource"

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
}

dependencies {
    implementation(libs.koinCore)

    implementation(platform(libs.supabaseBom))
    implementation(libs.supabasePostgres)
    implementation(libs.ktor)

    implementation(projects.datasource)
}
