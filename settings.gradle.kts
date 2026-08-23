enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ctm-reborn"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.teamresourceful.com/repository/maven-public/")
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
// NeoForge has published no 26.3 build - 26.2.0.66 is the newest that exists anywhere. Its userdev
// artifact pins its own NeoForm (26.2-2), which overrides the version set in the catalog, so leaving
// this enabled silently compiles the NeoForge jar against Minecraft 26.2 and labels it 26.3.
// The source is kept up to date; re-enable this line and set neoforge in gradle/libs.versions.toml
// once a 26.3 build lands.
// include("neoforge")