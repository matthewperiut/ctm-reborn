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
// NeoForge has published no build for 26.3 - 26.2.0.66 is the newest that exists anywhere, and its
// patches do not apply to 26.3 sources (23 patch targets are missing, the patch step exits non-zero).
// Pairing NeoForge 26.2 userdev with the 26.3 NeoForm is therefore not possible, so the module is left
// out until NeoForge ships a 26.3 build. The source is kept current; re-enable this line and set
// neoforge in gradle/libs.versions.toml when one lands. The version guard in build.gradle.kts will
// refuse the build if the two ever disagree again.
// include("neoforge")