object Kotlin {
    const val version = "1.6.10"
    const val gradlePlugin = "org.jetbrains.kotlin.android"
    const val parcelizePlugin = "kotlin-parcelize"
    const val androidPlugin = "kotlin-android"
    const val javaPlugin = "kotlin"
    const val annotationProcessor = "kotlin-kapt"

    object Coroutines {
        private const val version = "1.6.0"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    }
}

object Java {
    const val libraryPlugin = "java-library"
}

object Android {
    const val gradlePluginVersion = "7.1.2"
    const val applicationPlugin = "com.android.application"
    const val libraryPlugin = "com.android.library"
}

object AndroidX {
    const val core = "androidx.core:core-ktx:1.7.0"
    const val annotations = "androidx.annotation:annotation:1.3.0"
}

object Compose {
    const val version = "1.2.0-alpha06"

    const val ActivityCompose = "androidx.activity:activity-compose:1.4.0"

    const val Ui = "androidx.compose.ui:ui:$version"
    const val UiTooling = "androidx.compose.ui:ui-tooling:$version"
    const val Material = "androidx.compose.material:material:$version"
    const val UiToolingPreview = "androidx.compose.ui:ui-tooling-preview:$version"

    const val ViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"

    const val NavigationReimagined = "dev.olshevski.navigation:reimagined:1.1.0"
}

object Detekt {
    const val version = "1.20.0-RC1"
    const val gradlePlugin = "io.gitlab.arturbosch.detekt"
    const val ktLintPlugin = "io.gitlab.arturbosch.detekt:detekt-formatting:$version"
}

object AndroidGitVersion {
    const val version = "0.4.14"
    const val plugin = "com.gladed.androidgitversion"
}

object Koin {
    private const val version = "3.1.5"
    const val compose = "io.insert-koin:koin-androidx-compose:$version"
    const val android = "io.insert-koin:koin-android:$version"
    const val core = "io.insert-koin:koin-core:$version"
}
