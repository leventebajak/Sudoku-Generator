plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.leventebajak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}