import com.teamresourceful.publishing.GitHubPom
import com.teamresourceful.publishing.javaPublishing
import com.teamresourceful.utils.Platform
import com.teamresourceful.utils.getPlatform
import groovy.json.StringEscapeUtils

plugins {
    java
    id("maven-publish")
    alias(libs.plugins.resourceful.gradle)
    alias(libs.plugins.resourceful.minecraft) apply false
}


subprojects {
    apply(plugin = "maven-publish")

    val platform = getPlatform()

    when (platform) {
        Platform.COMMON -> apply(plugin = "com.teamresourceful.plugins.minecraft-platform-common")
        Platform.FABRIC -> apply(plugin = "com.teamresourceful.plugins.minecraft-platform-fabric")
        Platform.NEOFORGE -> apply(plugin = "com.teamresourceful.plugins.minecraft-platform-neoforge")
    }

    if (platform != Platform.COMMON) {
        tasks.withType<JavaCompile> {
            val serviceArgs = listOf(
                "-Xplugin:ServicePlugin",
                "--service-plugin-platform=$platform",
            )

            options.encoding = "UTF-8"
            options.compilerArgs.add(serviceArgs.joinToString(separator = " "))
        }
    }

    dependencies {
        if (platform != Platform.COMMON) {
            annotationProcessor(rootProject.libs.service.plugin)
        }
    }

    javaPublishing {
        artifactId = "${rootProject.name}-${platform.name}-${rootProject.libs.versions.minecraft.get()}".lowercase()

        pom = GitHubPom(
            "CTM Reborn $platform",
            "A multiplatform baked model library for Minecraft mods, forked from Athena",
            "MIT",
            "https://github.com/matthewperiut/ctm-reborn"
        )

        repo = "https://maven.pkg.github.com/matthewperiut/ctm-reborn"
    }
}

resourcefulGradle {
    templates {
        register("embed") {
            val changelog: String = file("changelog.md").readText(Charsets.UTF_8)

            source = file("templates/embed.json.template")
            injectedValues = mapOf(
                "version" to version,
                "minecraft" to rootProject.libs.versions.minecraft.get(),
                "fabric_link" to System.getenv("FABRIC_RELEASE_URL"),
                "neoforge_link" to System.getenv("NEOFORGE_RELEASE_URL"),
                "changelog" to StringEscapeUtils.escapeJava(changelog),
            )
        }
    }
}