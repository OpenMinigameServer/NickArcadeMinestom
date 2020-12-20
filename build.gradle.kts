import kr.entree.spigradle.kotlin.paper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("kr.entree.spigradle") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "io.github.nickacpt"
version = "1.0-SNAPSHOT"

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss"
    }

    mavenCentral()
}

val cloudVersion = "1.3.0"
val configurateVersion = "4.0.0"
val kMongoVersion = "4.2.3"
val adventureVersion = "4.2.0-SNAPSHOT"
dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    compileOnly(paper("1.16.4"))

    implementation(project(":Hypixel-API"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:0.0.5")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:0.0.5")

    implementation("cloud.commandframework:cloud-paper:$cloudVersion")
    implementation("cloud.commandframework:cloud-annotations:$cloudVersion")

    implementation("org.spongepowered:configurate-yaml:$configurateVersion")
    implementation("org.spongepowered:configurate-extra-kotlin:$configurateVersion")

    implementation("org.litote.kmongo:kmongo-coroutine:$kMongoVersion")

    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")


}
tasks {

    /*create<ConfigureShadowRelocation>("relocateShadowJar") {
        prefix = "dependencies"
        target = shadowJar.get()
    }

    shadowJar.get().dependsOn(get("relocateShadowJar"))*/

    shadowJar {
        assemble.get().dependsOn(this)
        archiveClassifier.set(null as String?)

        relocate("kotlin", "kotlin")
    }

    spigot {
        name = "NickArcade"
        authors = listOf("NickAc")

        apiVersion = "1.16"
        version = project.version.toString()

        softDepends("ViaVersion")

        debug {
            eula = true
            buildVersion = "1.16.4"
        }
    }
}


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.4"
    jvmTarget = "1.8"
    freeCompilerArgs =
        listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xopt-in=kotlin.time.ExperimentalTime", "-Xinline-classes")
}