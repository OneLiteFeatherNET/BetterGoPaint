rootProject.name = "BetterGoPaint"
includeBuild("build-logic")

pluginManagement {
    repositories {
        maven("https://eldonexus.de/repository/maven-public/")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("publishdata", "1.4.0")
            version("modrinth", "2.+")
            version("hangar", "0.1.2")
            version("paper.yml", "0.6.0")
            version("paper.run", "2.3.0")
            version("shadowJar", "8.1.1")
            version("intellectualsites", "1.51")

            version("paper", "1.21.3-R0.1-SNAPSHOT")
            version("bstats", "3.0.2")

            version("cloudcommand", "2.0.0-SNAPSHOT")

            version("adventure", "4.17.0")
            version("semver", "0.10.2")

            library("paper", "io.papermc.paper", "paper-api").versionRef("paper")
            library("minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("bstats", "org.bstats", "bstats-bukkit").versionRef("bstats")

            library("fawe.bom", "com.intellectualsites.bom", "bom-newest").versionRef("intellectualsites")
            library("fawe.bukkit", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").withoutVersion()
            library("serverlib", "dev.notmyfault.serverlib", "ServerLib").withoutVersion()
            library("paperlib", "io.papermc", "paperlib").withoutVersion()

            library("cloud.command.paper", "org.incendo", "cloud-paper").versionRef("cloudcommand")
            library("cloud.command.annotations", "org.incendo", "cloud-annotations").versionRef("cloudcommand")
            library("cloud.command.extras", "org.incendo", "cloud-minecraft-extras").versionRef("cloudcommand")

            library("semver", "com.github.zafarkhaja", "java-semver").versionRef("semver")

            plugin("publishdata","de.chojo.publishdata").versionRef("publishdata")
            plugin("modrinth", "com.modrinth.minotaur").versionRef("modrinth")
            plugin("hangar", "io.papermc.hangar-publish-plugin").versionRef("hangar")
            plugin("paper.yml", "net.minecrell.plugin-yml.paper").versionRef("paper.yml")
            plugin("paper.run", "xyz.jpenilla.run-paper").versionRef("paper.run")
            plugin("shadowJar", "com.github.johnrengelman.shadow").versionRef("shadowJar")
            plugin("spotless", "com.diffplug.spotless").version("6.18.0")
        }
    }
}
