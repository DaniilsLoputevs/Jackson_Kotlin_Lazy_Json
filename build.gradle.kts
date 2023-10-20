import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/io.kotest/kotest-runner-junit5-jvm
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")

    jackson()

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "19"
}

application {
    mainClass.set("MainKt")
}

fun DependencyHandler.jackson() {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.5")
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.12.5")
}