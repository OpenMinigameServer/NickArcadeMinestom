import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.zeroturnaround.gradle.jrebel") version "1.1.10"
    application
}

group = "io.github.nickacpt"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://maven.enginehub.org/repo/")
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://libraries.minecraft.net") }
    maven { setUrl("https://repo.spongepowered.org/maven") }
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://kotlin.bintray.com/kotlinx/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss"
    }

    mavenCentral()
}

val cloudVersion = "1.4.0"
val configurateVersion = "4.0.0"
val kMongoVersion = "4.2.3"
val adventureVersion = "4.2.0-SNAPSHOT"
val minestomVersion = "7a54b4162d"
dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(minestom(minestomVersion))
    compileOnly("org.jetbrains:annotations:20.1.0")

    implementation(project(":Hypixel-API"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    implementation("com.github.OpenMinigameServer:cloud-minestom:58e8fd76f3")

    implementation("cloud.commandframework:cloud-annotations:$cloudVersion") {
        exclude(module = "geantyref")
    }
    implementation("cloud.commandframework:cloud-kotlin-extensions:$cloudVersion") {
        exclude(module = "geantyref")
    }

    implementation("org.spongepowered:configurate-yaml:$configurateVersion") {
        exclude(module = "geantyref")
    }
    implementation("org.spongepowered:configurate-extra-kotlin:$configurateVersion") {
    }

    implementation("org.litote.kmongo:kmongo-coroutine:$kMongoVersion")

    implementation("net.kyori:adventure-api:4.3.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-platform-api:4.0.0-SNAPSHOT")
    implementation("com.github.mworzala:adventure-platform-minestom:b61596ccef") {
        exclude(module = "Minestom")
    }
    implementation("com.github.OpenMinigameServer:MinestomWorldEdit:d667d61c63")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))
    implementation("com.github.OpenMinigameServer.Replay:Replay:f9bc4e80da")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

tasks {

    application {

        mainClassName = ("io.github.nickacpt.nickarcade.application.NickArcadeKt")
    }
/*
    create<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
        prefix = "dependencies"
        target = shadowJar.get()
    }

    shadowJar.get().dependsOn(get("relocateShadowJar"))*/

    shadowJar {
        assemble.get().dependsOn(this)
        archiveClassifier.set(null as String?)

        relocate("kotlin", "kotlin")
        relocate("org.spongepowered.configurate", "dependencies.org.spongepowered.configurate")
        relocate("org.reactivestreams", "dependencies.org.reactivestreams")
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.4"
    jvmTarget = "1.8"
    freeCompilerArgs =
        listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xopt-in=kotlin.time.ExperimentalTime", "-Xinline-classes")
}


fun minestom(commit: String): String {
    return "com.github.Minestom:Minestom:$commit"
}
