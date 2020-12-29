plugins {
    kotlin("jvm")
    maven
}

group = "io.github.nickacpt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.+")

    implementation("com.google.guava:guava:30.0-jre")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}