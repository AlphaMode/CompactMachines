plugins {
    java
    id("fabric-loom") version ("1.0-SNAPSHOT")
    id("io.github.juuxel.loom-quiltflower") version ("1.+") // Quiltflower, a better decompiler
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

var minecraft_version: String by extra
var loader_version: String by extra
var fabric_version: String by extra
var port_lib_version: String by extra

project.evaluationDependsOn(project(":forge-api").path)

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
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    modCompileOnly("io.github.fabricators_of_create.Porting-Lib:porting-lib:${port_lib_version}+${minecraft_version}")

    implementation(project(":forge-api"))

    for (project: Project in subprojects) {
        runtimeOnly(project as Dependency) {
            (this as ExternalModuleDependency).isTransitive = false
        }
        include(project)
    }
}

loom.accessWidenerPath.set(file("../forge-main/src/main/resources/compactmachines.accesswidener"))