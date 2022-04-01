plugins {
    id(Android.applicationPlugin).version(Android.gradlePluginVersion).apply(false)
    id(Kotlin.gradlePlugin).version(Kotlin.version).apply(false)
    id(Detekt.gradlePlugin).version(Detekt.version)
}

subprojects {
    apply(plugin = Detekt.gradlePlugin)

    detekt {
        debug = true
        buildUponDefaultConfig = true
        ignoreFailures = true
        config = files("$rootDir/config/detekt.yml")
        dependencies {
            detektPlugins(Detekt.ktLintPlugin)
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
