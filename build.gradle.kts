plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:2.2.4")
    implementation("io.ktor:ktor-client-java:2.2.4")
    implementation("ch.qos.logback:logback-classic:1.2.9")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("org.jsoup:jsoup:1.15.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}