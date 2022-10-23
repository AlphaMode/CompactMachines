
import java.text.SimpleDateFormat
import java.util.*

val semver: String = System.getenv("CM_SEMVER_VERSION") ?: "9.9.9"
val buildNumber: String = System.getenv("CM_BUILD_NUM") ?: "0"
val nightlyVersion: String = "${semver}.${buildNumber}-nightly"
val isRelease: Boolean = (System.getenv("CM_RELEASE") ?: "false").equals("true", true)

var mod_id: String by extra

plugins {
    id("idea")
    id("eclipse")
    id("maven-publish")
    id("fabric-loom") version ("1.0-SNAPSHOT")
    id("io.github.juuxel.loom-quiltflower") version ("1.+") // Quiltflower, a better decompiler
}

base {
    archivesName.set(mod_id)
    group = "dev.compactmods"
    version = if(isRelease) semver else nightlyVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

var minecraft_version: String by extra
var loader_version: String by extra
var fabric_version: String by extra
var port_lib_version: String by extra

sourceSets {
//    named("main") {
//        resources {
//            //The API has no resources
//            setSrcDirs(emptyList<String>())
//        }
//    }

    named("test") {
        resources {
            //The test module has no resources
            setSrcDirs(emptyList<String>())
        }
    }
}

loom.accessWidenerPath.set(file("../forge-main/src/main/resources/compactmachines.accesswidener"))

repositories {
    maven("https://mvn.devos.one/snapshots/") // Porting Lib, Forge Tags, Tinkers, Mantle
    maven("https://ladysnake.jfrog.io/artifactory/mods") // Cardinal Components API
    maven("https://maven.parchmentmc.net/") // Parchment
    maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
    maven("https://jitpack.io") // Fabric ASM, Mixin Extras
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    modImplementation("io.github.fabricators_of_create.Porting-Lib:porting-lib:${port_lib_version}+${minecraft_version}")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    modApi("teamreborn:energy:2.2.0") {
        exclude("net.fabricmc.fabric-api")
    }

    for (project: Project in subprojects) {
        runtimeOnly(project as Dependency) {
            (this as ExternalModuleDependency).isTransitive = false
        }
        include(project)
    }
}

tasks.withType<Jar> {
    manifest {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        attributes(mapOf(
                "Specification-Title" to "Compact Machines API",
                "Specification-Vendor" to "",
                "Specification-Version" to "1", // We are version 1 of ourselves
                "Implementation-Title" to "Compact Machines API",
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to "",
                "Implementation-Timestamp" to now
        ))
    }
}

tasks.jar {
    archiveClassifier.set("api")
    finalizedBy("remapJar")
}

tasks.named<Jar>("sourcesJar") {
    archiveClassifier.set("api-sources")
}

artifacts {
    archives(tasks.jar.get())
    archives(tasks.named("sourcesJar").get())
}

publishing {
    publications.register<MavenPublication>("releaseApi") {
        artifactId = "compactmachines"
        groupId = "dev.compactmods"

        artifacts {
            artifact(tasks.jar.get())
            artifact(tasks.named("sourcesJar").get())
        }
    }

    repositories {
        // GitHub Packages
        maven("https://maven.pkg.github.com/CompactMods/CompactMachines") {
            name = "GitHubPackages"
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
