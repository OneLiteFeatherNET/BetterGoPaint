import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    jacoco
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.paper.run)
    alias(libs.plugins.paper.yml)
    alias(libs.plugins.hangar)
    alias(libs.plugins.modrinth)
    alias(libs.plugins.cyclonedx)
    alias(libs.plugins.spotless)
}

group = "net.onelitefeather.bettergopaint"
version = "1.1.0" // x-release-please-version

if (!File("$rootDir/.git").exists()) {
    logger.lifecycle(
        """
    **************************************************************************************
    You need to fork and clone this repository! Don't download a .zip file.
    If you need assistance, consult the GitHub docs: https://docs.github.com/get-started/quickstart/fork-a-repo
    **************************************************************************************
    """.trimIndent()
    ).also { System.exit(1) }
}

// Every version listed here is offered on Hangar and Modrinth and gets a run-<version> task.
// Paper 26.x runs on Java 25, which is also the bytecode level this plugin is compiled for,
// so older Minecraft lines cannot load the jar. Keep docs/reference/supported-versions.md in sync.
val supportedMinecraftVersions = listOf(
    "26.1.1",
    "26.1.2",
    "26.2"
)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.fawe.core)
    compileOnly(libs.fawe.bukkit) { isTransitive = false }

    implementation(libs.bstats)
    implementation(libs.semver)
    implementation(libs.cloud.command.annotations)
    implementation(libs.cloud.command.extras)
    implementation(libs.cloud.command.paper)
    annotationProcessor(libs.cloud.command.annotations)

    testImplementation(libs.paper)
    testImplementation(libs.fawe.core)
    testImplementation(libs.fawe.bukkit) { isTransitive = false }
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.platform.launcher)
}

paper {
    name = "BetterGoPaint"
    main = "net.onelitefeather.bettergopaint.BetterGoPaint"
    authors = listOf("Arcaniax", "TheMeinerLP", "OneLiteFeather")
    apiVersion = "26.1"
    website = "https://github.com/OneLiteFeatherNET/BetterGoPaint"
    provides = listOf("goPaint")

    serverDependencies {
        register("FastAsyncWorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }

    permissions {
        register("bettergopaint.use") {
            description = "Use the goPaint brush. Every /gopaint subcommand also needs its own node."
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("bettergopaint.command.help") {
            description = "List the /gopaint commands with /gopaint help."
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("bettergopaint.command.size") {
            description = "Set the brush size with /gopaint size <size>."
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("bettergopaint.command.toggle") {
            description = "Enable or disable the brush with /gopaint toggle."
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("bettergopaint.command.info") {
            description = "Show the plugin credits with /gopaint info."
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("bettergopaint.command.admin.reload") {
            description = "Reload config.yml with /gopaint reload."
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("bettergopaint.admin") {
            description = "Bundle of the administrative permissions."
            children = listOf("bettergopaint.command.admin.reload")
            default = BukkitPluginDescription.Permission.Default.FALSE
        }
        register("bettergopaint.world.bypass") {
            description = "Paint in worlds listed under disabled worlds in config.yml."
            default = BukkitPluginDescription.Permission.Default.FALSE
        }
        register("bettergopaint.notify.admin.update") {
            description = "Receive a notice on join when a newer plugin version is available on Hangar."
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("bettergopaint.notify.disable.donation") {
            description = "Suppress the donation notice that is shown on join."
            default = BukkitPluginDescription.Permission.Default.FALSE
        }
    }
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        target("src/**/*.java")
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
    compileTestJava {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
    jar {
        archiveClassifier.set("unshaded")
    }
    shadowJar {
        archiveClassifier.set("")
        relocate("org.bstats", "${project.group}.metrics")
        relocate("com.github.zafarkhaja.semver", "${project.group}.semver")
    }
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }
        finalizedBy(jacocoTestReport)
    }
    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
    named("modrinth") {
        dependsOn(shadowJar)
    }
    named("publishAllPublicationsToHangar") {
        dependsOn(shadowJar)
    }
    supportedMinecraftVersions.forEach { serverVersion ->
        register<RunServer>("run-$serverVersion") {
            minecraftVersion(serverVersion)
            jvmArgs("-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true")
            group = "run paper"
            runDirectory.set(file("run-$serverVersion"))
            pluginJars(shadowJar.flatMap { it.archiveFile })
            downloadPlugins {
                modrinth("fastasyncworldedit", libs.versions.fawe.get())
            }
        }
    }
}

val baseVersion = version as String
val baseChannel = with(baseVersion) {
    when {
        contains("SNAPSHOT", true) -> "Snapshot"
        contains("ALPHA", true) -> "Alpha"
        contains("BETA", true) -> "Beta"
        else -> "Release"
    }
}
val changelogContent = "See [GitHub](https://github.com/OneLiteFeatherNET/BetterGoPaint/releases/tag/v$baseVersion) for release notes."

hangarPublish {
    publications.register("BetterGoPaint") {
        version.set(baseVersion)
        channel.set(baseChannel)
        changelog.set(changelogContent)
        apiKey.set(System.getenv("HANGAR_SECRET"))
        id.set("BetterGoPaint")
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions.set(supportedMinecraftVersions)
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("qf7sNg9A")
    versionType.set(baseChannel.lowercase())
    versionNumber.set(baseVersion)
    versionName.set(baseVersion)
    changelog.set(changelogContent)
    uploadFile.set(tasks.shadowJar.flatMap { it.archiveFile })
    gameVersions.addAll(supportedMinecraftVersions)
    loaders.add("paper")
}
