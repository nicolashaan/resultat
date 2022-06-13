pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
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
