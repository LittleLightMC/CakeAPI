import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.5.10"

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "pro.darc.cake"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    google()
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
//    implementation("org.reflections:reflections:0.9.12")
    implementation("net.oneandone.reflections8:reflections8:0.11.7")
    implementation("org.javassist:javassist:3.28.0-GA")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-native-mt")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.2.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0-SNAPSHOT")
    implementation("org.mongodb:mongodb-driver-reactivestreams:4.2.3")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.2.8")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi,kotlinx.coroutines.ExperimentalCoroutinesApi"
}

tasks.shadowJar {
    relocate("org.bstats", "pro.darc.cake")
}

tasks.processResources {
    expand(
        "plugin_main_path" to "${project.group}.${project.name}",
        "plugin_version" to project.version,
        "plugin_name" to project.name
    )
}