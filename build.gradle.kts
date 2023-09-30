plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.detekt)
}

apply(plugin = libs.plugins.detekt.get().pluginId)

detekt {
    debug = true
    buildUponDefaultConfig = true
    ignoreFailures = true
    config = files("$rootDir/config/detekt.yml")
    dependencies {
        detektPlugins(libs.plugins.detektFormatting.get().pluginId)
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

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
