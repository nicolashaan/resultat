plugins {
    kotlin("multiplatform") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("org.jetbrains.dokka") version "1.6.21"
    id("maven-publish")
    id("signing")
}

version = "1.0.0-rc1"
group = "fr.haan.resultat"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }

    js(LEGACY) {
        nodejs()
        browser()
    }

    listOf(
        iosArm32(),
        iosArm64(),
        iosX64(),
        watchosArm32(),
        watchosArm64(),
        watchosX86(),
        watchosX64(),
        tvosArm64(),
        tvosX64(),
        macosX64("macOS"),
        macosArm64("macOSArm"),
        iosSimulatorArm64(),
        watchosSimulatorArm64(),
        tvosSimulatorArm64(),
    ).forEach {
        it.binaries {
            framework {
                baseName = "Resultat"
            }
        }
    }

    mingwX64()
    mingwX86()
    linuxX64()
    linuxArm32Hfp()
    linuxMips32()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(rootDir.resolve("docs"))
}

val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Javadoc JAR"
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml"))
}

publishing {
    publications.withType(MavenPublication::class) {

        artifact(javadocJar)

        pom {
            name.set("Résultat")
            description.set("Resultat is kotlin.Result with loading state")
            url.set("https://github.com/nicolashaan/resultat")
            licenses {
                license {
                    name.set("Résultat License")
                    url.set("https://github.com/nicolashaan/resultat/blob/main/LICENCE.md")
                }
            }
            developers {
                developer {
                    id.set("nicolashaan")
                    name.set("Nicolas Haan")
                }
            }

            scm {
                connection.set("scm:git:github.com/nicolashaan/resultat.git")
                developerConnection.set("scm:git:ssh://github.com/nicolashaan/resultat.git")
                url.set("https://github.com/nicolashaan/resultat/tree/main")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}