pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "granite"

include(
    ":app",
    ":domain",
    ":local-datasource",
    ":supabase-datasource",
    ":ui-components",
    ":markdown-renderer",
    ":markdown-obsidian-parser",
    ":fuzzy-search",
)
