import java.text.SimpleDateFormat
import java.util.*

val semver: String = System.getenv("CM_SEMVER_VERSION") ?: "9.9.9"
val buildNumber: String = System.getenv("CM_BUILD_NUM") ?: "0"

val nightlyVersion: String = "${semver}.${buildNumber}-nightly"
val isRelease: Boolean = (System.getenv("CM_RELEASE") ?: "false").equals("true", true)

var mod_id: String by extra
var minecraft_version: String by extra
var loader_version: String by extra
var fabric_version: String by extra
var port_lib_version: String by extra
var night_config_version: String by extra
var config_api_version: String by extra
var cca_version: String by extra
var trinkets_version: String by extra

tasks.create("getBuildInfo") {
    println("Mod ID: ${mod_id}")
    println("Version: ${version}")
    println("Semver Version: ${semver}")
    println("Nightly Build: ${nightlyVersion}")
}

plugins {
    id("idea")
    id("eclipse")
    id("maven-publish")
    id("fabric-loom") version ("1.0-SNAPSHOT")
}

base {
    archivesName.set(mod_id)
    group = "dev.compactmods"
    version = if(isRelease) semver else nightlyVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

sourceSets.main {
    resources {
        srcDir("src/main/resources")
        srcDir("src/generated/resources")
    }
}

sourceSets.test {
    java.srcDir("src/test/java")
    resources.srcDir("src/test/resources")
}

loom {
    runs {
        create("datagen") {
            client()

            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=compactmachines")
            // from ae2
            property("dev.compactmods.machines.datagen.existingData", file("src/main/resources").absolutePath)

            runDir("build/datagen")
        }
        create("gametest") {
            server()
            name("Game Test")
            vmArg("-Dfabric-api.gametest.server=true")
        }
    }
}

project.evaluationDependsOn(project(":forge-api").path)

repositories {
    mavenLocal()
    mavenCentral() {
        content {
            includeGroup("com.aventrix.jnanoid")
        }
    }

    maven("https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }

    // location of the maven that hosts JEI files
    maven("https://dvs1.progwml6.com/files/maven") {
        content {
            includeGroup("mezz.jei")
        }
    }

    maven("https://maven.theillusivec4.top/") {
        content {
            includeGroup("top.theillusivec4.curios")
        }
    }
    // TheOneProbe
    maven("https://maven.wispforest.io") {
        content {
            includeGroup("mcjty.theoneprobe")
        }
    }
    maven("https://ladysnake.jfrog.io/artifactory/mods") {
        content {
            includeGroup("dev.onyxstudios.cardinal-components-api")
        }
    }

    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") {
        content {
            includeGroup("net.minecraftforge")
        }
    }

    maven("https://maven.terraformersmc.com/")
    maven("https://mvn.devos.one/snapshots/") // Porting Lib, Forge Tags, Tinkers, Mantle
    maven("https://ladysnake.jfrog.io/artifactory/mods") // Cardinal Components API
    maven("https://maven.parchmentmc.net/") // Parchment
    maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
    maven("https://jitpack.io") // Fabric ASM, Mixin Extras
}

val jei_version: String? by extra
val jei_mc_version: String by extra
val top_version: String? by extra

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    modImplementation("io.github.fabricators_of_create.Porting-Lib:porting-lib:${port_lib_version}+${minecraft_version}")
    include("io.github.fabricators_of_create.Porting-Lib:porting-lib:${port_lib_version}+${minecraft_version}")
    modImplementation("com.electronwill.night-config:core:${night_config_version}")
    modImplementation("com.electronwill.night-config:toml:${night_config_version}")
    modImplementation("net.minecraftforge:forgeconfigapiport-fabric:${config_api_version}"){isTransitive = false}
    include("com.electronwill.night-config:core:${night_config_version}")
    include("com.electronwill.night-config:toml:${night_config_version}")
    include("net.minecraftforge:forgeconfigapiport-fabric:${config_api_version}")
    modImplementation("io.github.tropheusj:serialization-hooks:0.3.22")
    modApi("teamreborn:energy:2.2.0") {
        exclude("net.fabricmc.fabric-api")
    }
    include("teamreborn:energy:2.2.0") {
        exclude("net.fabricmc.fabric-api")
    }

    implementation("com.google.code.findbugs:jsr305:3.0.2")
    include("com.google.code.findbugs:jsr305:3.0.2")

    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${cca_version}")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${cca_version}")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-block:${cca_version}")
    include("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${cca_version}")
    include("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${cca_version}")
    include("dev.onyxstudios.cardinal-components-api:cardinal-components-block:${cca_version}")

    modImplementation("dev.emi:trinkets:${trinkets_version}"){isTransitive = false}

    modImplementation(project(":forge-api")){isTransitive = false}
    include(project(":forge-api"))
    testImplementation(project(":forge-api"))

    modImplementation(project(":forge-tunnels")){isTransitive = false}
    include(project(":forge-tunnels")){isTransitive = false}
    testImplementation(project(":forge-tunnels"))

    // JEI
    if (project.extra.has("jei_version") && project.extra.has("jei_mc_version")) {
        modCompileOnlyApi("mezz.jei:jei-${jei_mc_version}-common-api:${jei_version}")
        modCompileOnlyApi("mezz.jei:jei-${jei_mc_version}-fabric-api:${jei_version}")
        modRuntimeOnly("mezz.jei:jei-${jei_mc_version}-fabric:${jei_version}")
    }

    // The One Probe
    modImplementation("mcjty.theoneprobe:theoneprobe-fabric:${top_version}")

    // Curios
//    if (project.extra.has("curios_version")) {
//        runtimeOnly(fg.deobf("top.theillusivec4.curios:curios-forge:${curios_version}"))
//        compileOnly(fg.deobf("top.theillusivec4.curios:curios-forge:${curios_version}:api"))
//    }

    implementation("com.aventrix.jnanoid", "jnanoid", "2.0.0")
    include("com.aventrix.jnanoid", "jnanoid", "[2.0.0]")

    val include_test_mods: String? by extra
    if (!System.getenv().containsKey("CI") && include_test_mods.equals("true")) {
        // Nicephore - Screenshots and Stuff
//        runtimeOnly(fg.deobf("curse.maven:nicephore-401014:3879841"))

        // Testing Mods - Trash Cans, Pipez, Create, Refined Pipes, Pretty Pipes, Refined Storage
//        runtimeOnly(fg.deobf("curse.maven:SuperMartijn642-454372:3910759"))
//        runtimeOnly(fg.deobf("curse.maven:trashcans-394535:3871885"))

        // runtimeOnly(fg.deobf("curse.maven:flywheel-486392:3871082"))
        // runtimeOnly(fg.deobf("curse.maven:create-328085:3737418"))

        // runtimeOnly(fg.deobf("curse.maven:refinedpipes-370696:3570151"))
        // runtimeOnly(fg.deobf("curse.maven:prettypipes-376737:3573145"))
        // runtimeOnly(fg.deobf("curse.maven:refinedstorage-243076:3623324"))

        // Scalable Cat's Force, BdLib, Advanced Generators
        // runtimeOnly(fg.deobf("curse.maven:scalable-320926:3634756"))
        // runtimeOnly(fg.deobf("curse.maven:bdlib-70496:3663149"))
        // runtimeOnly(fg.deobf("curse.maven:advgen-223622:3665335"))

        // Immersive Eng - 7.1.0-145 (Dec 31)
        // runtimeOnly(fg.deobf("curse.maven:immersiveeng-231951:3587149"))

        // FTB Chunks
        // runtimeOnly(fg.deobf("curse.maven:architectury-forge-419699:3781711"))
        // runtimeOnly(fg.deobf("curse.maven:ftb-teams-404468:3725501"))
        // runtimeOnly(fg.deobf("curse.maven:ftblib-404465:3725485"))
        // runtimeOnly(fg.deobf("curse.maven:ftbchunks-314906:3780113"))

        // Mekanism + Mek Generators - Tunnel testing
//        runtimeOnly(fg.deobf("curse.maven:mekanism-268560:3922056"))
//        runtimeOnly(fg.deobf("curse.maven:mekanismgenerators-268566:3922058"))

        // Soul Shards (FTB)
        // runtimeOnly(fg.deobf("curse.maven:polylib-576589:3751528"))
        // runtimeOnly(fg.deobf("curse.maven:soulshards-551523:3757202"))

        // Everlasting Abilities
        // runtimeOnly(fg.deobf("curse.maven:cyclopscore-232758:3809427"))
        // runtimeOnly(fg.deobf("curse.maven:everlastabilities-248353:3768481"))
    }

    for (project: Project in subprojects) {
        runtimeOnly(project as Dependency) {
            (this as ExternalModuleDependency).isTransitive = false
        }
        include(project)
    }
}

val runDepends: List<Project> = listOf(
        project(":forge-api"),
        project(":forge-tunnels")
)

runDepends.forEach {
    project.evaluationDependsOn(it.path)
}

loom.accessWidenerPath.set(file("src/main/resources/compactmachines.accesswidener"))

tasks.compileJava {
    options.encoding = "UTF-8";
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val properties: HashMap<String, String> = HashMap();
    properties.put("version", version as String)
    properties.put("loader_version", loader_version)
    properties.put("fabric_version", fabric_version)
    properties.put("minecraft_version", minecraft_version)
    properties.put("port_lib_version", "${port_lib_version}+${minecraft_version}")

    properties.forEach { (k, v) ->
        inputs.property(k, v)
    }

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

tasks.withType<Jar> {
    // Remove datagen source and cache info
    this.exclude("dev/compactmods/machines/datagen/**")
    this.exclude(".cache/**")

    // TODO - Switch to API jar when JarInJar supports it better
    val api = project(":forge-api").tasks.jar.get().archiveFile;
    from(api.map { zipTree(it) })

    val tunnels = project(":forge-tunnels").tasks.jar.get().archiveFile;
    from(tunnels.map { zipTree(it) })

    manifest {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        attributes(mapOf(
                "Specification-Title" to "Compact Machines",
                "Specification-Vendor" to "",
                "Specification-Version" to "1", // We are version 1 of ourselves
                "Implementation-Title" to "Compact Machines",
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to "",
                "Implementation-Timestamp" to now
        ))
    }
}

tasks.jar {
    archiveClassifier.set("slim")
//    finalizedBy("remapJar")
}

artifacts {
    archives(tasks.jar.get())
}

publishing {
    publications.register<MavenPublication>("releaseMain") {
        artifactId = mod_id
        groupId = "dev.compactmods"

        artifacts {
            artifact(tasks.jar.get())
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