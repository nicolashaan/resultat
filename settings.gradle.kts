pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins  {
        val kotlinVersion: String by settings
        kotlin("multiplatform") version kotlinVersion
        id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
        id("org.jetbrains.dokka") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        id("maven-publish")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral ()
    }
}


rootProject.name = "resultat-project"
include("resultat")
include("samples:sample-jvm")

findProject(":samples:sample-jvm")?.name = "sample-jvm"
