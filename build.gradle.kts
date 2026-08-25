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

    // NeoForge pins the NeoForm (and therefore the Minecraft) version inside its own userdev artifact,
    // and that pin wins over the version catalog. On a Minecraft version NeoForge has not shipped for yet
    // that does not fail - it quietly builds the NeoForge jar against whatever Minecraft the newest
    // NeoForge targets, while the rest of the project is on the newer one, and reports success.
    //
    // Fail loudly instead. If this trips, NeoForge has no build for the Minecraft version in the catalog.
    if (platform == Platform.NEOFORGE) {
        afterEvaluate {
            val expected = rootProject.libs.versions.minecraft.get()
            val neoForge = extensions.findByName("neoForge")
            val actual = neoForge?.let {
                runCatching { it.javaClass.getMethod("getMinecraftVersion").invoke(it) as? String }.getOrNull()
            }

            if (actual != null && actual != expected) {
                throw GradleException(
                    "NeoForge is building against Minecraft $actual, but this project targets $expected.\n" +
                        "NeoForge ${rootProject.libs.versions.neoforge.get()} pins Minecraft $actual, so the jar would " +
                        "be compiled against the wrong game version and still be labelled $expected.\n" +
                        "Wait for a NeoForge build targeting $expected, or drop the neoforge module from " +
                        "settings.gradle.kts until one exists."
                )
            }
        }
    }

    // Both platform jars would otherwise be built as ctm-reborn-<version>.jar. That is ambiguous on
    // Modrinth and CurseForge, where the two files are then indistinguishable by name, and on a GitHub
    // release it is a collision that leaves only one of them attached.
    if (platform != Platform.COMMON) {
        configure<BasePluginExtension> {
            archivesName.set("${rootProject.name}-${platform.id}")
        }
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