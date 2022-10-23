pluginManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.quiltmc.org/repository/release")
        maven("https://server.bbkr.space/artifactory/libs-release/")
    }
}

rootProject.name = "Compact Machines"
include("forge-api", "forge-main", "forge-tunnels")