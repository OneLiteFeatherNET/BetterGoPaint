rootProject.name = "BetterGoPaint"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
    }
    versionCatalogs {
        create("libs") {
            version("modrinth", "2.+")
            version("hangar", "0.1.4")
            version("paper.yml", "0.9.0")
            version("paper.run", "3.1.0")
            version("shadowJar", "9.6.1")
            version("cyclonedx", "3.4.1")
            version("spotless", "8.10.4")

            // Compile against the newest stable Paper line. Paper 26.x requires Java 25.
            version("paper", "26.2.build.+")
            version("fawe", "2.16.1")
            version("bstats", "3.2.1")

            version("cloudcommandPaper", "2.0.1")
            version("cloudcommandAnnotations", "2.1.0")
            version("cloudcommandExtras", "2.0.1")

            version("semver", "0.10.2")
            version("guice", "7.0.0")
            version("jakarta-inject", "2.0.1")

            // Testing dependencies
            version("junit", "6.1.3")
            version("mockito", "5.24.0")
            version("assertj", "3.27.7")

            library("paper", "io.papermc.paper", "paper-api").versionRef("paper")
            library("fawe.core", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").versionRef("fawe")
            library("fawe.bukkit", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").versionRef("fawe")
            library("bstats", "org.bstats", "bstats-bukkit").versionRef("bstats")

            library("cloud.command.paper", "org.incendo", "cloud-paper").versionRef("cloudcommandPaper")
            library("cloud.command.annotations", "org.incendo", "cloud-annotations").versionRef("cloudcommandAnnotations")
            library("cloud.command.extras", "org.incendo", "cloud-minecraft-extras").versionRef("cloudcommandExtras")

            library("semver", "com.github.zafarkhaja", "java-semver").versionRef("semver")
            library("guice", "com.google.inject", "guice").versionRef("guice")
            library("jakarta-inject", "jakarta.inject", "jakarta.inject-api").versionRef("jakarta-inject")

            // Testing libraries
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-platform-launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()
            library("mockito-core", "org.mockito", "mockito-core").versionRef("mockito")
            library("mockito-junit-jupiter", "org.mockito", "mockito-junit-jupiter").versionRef("mockito")
            library("assertj-core", "org.assertj", "assertj-core").versionRef("assertj")

            plugin("modrinth", "com.modrinth.minotaur").versionRef("modrinth")
            plugin("hangar", "io.papermc.hangar-publish-plugin").versionRef("hangar")
            plugin("paper.yml", "de.eldoria.plugin-yml.paper").versionRef("paper.yml")
            plugin("paper.run", "xyz.jpenilla.run-paper").versionRef("paper.run")
            plugin("shadowJar", "com.gradleup.shadow").versionRef("shadowJar")
            plugin("cyclonedx", "org.cyclonedx.bom").versionRef("cyclonedx")
            plugin("spotless", "com.diffplug.spotless").versionRef("spotless")
        }
    }
}
