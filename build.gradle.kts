import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "io.github.nickacpt"
version = "1.0-SNAPSHOT"

repositories {

    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://libraries.minecraft.net") }
    maven { setUrl("https://repo.spongepowered.org/maven") }
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

    implementation(minestom("9546caca04"))
    compileOnly("org.jetbrains:annotations:20.1.0")

    implementation(project(":Hypixel-API"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

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
    implementation("net.kyori:adventure-platform-api:4.0.0-SNAPSHOT")
    implementation("com.github.mworzala:adventure-platform-minestom:f1d1c3adc5")
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
