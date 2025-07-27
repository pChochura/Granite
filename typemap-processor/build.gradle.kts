plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(libs.ksp)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoetKsp)
}
