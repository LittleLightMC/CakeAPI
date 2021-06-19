import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.5.10"

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

group = "pro.darc.cake"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    implementation("org.reflections:reflections:0.9.12")
    implementation("org.javassist:javassist:3.28.0-GA")
    kapt("me.tatarka.inject:kotlin-inject-compiler-ksp:0.3.5")
    implementation("me.tatarka.inject:kotlin-inject-runtime:0.3.5")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi,kotlinx.coroutines.ExperimentalCoroutinesApi"
}

tasks.shadowJar {
    archiveBaseName.set("CakeAPI-shade")
    dependencies {
        include(dependency("org.reflections:reflections:0.9.12"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
        include(dependency("org.javassist:javassist:3.28.0-GA"))
    }
    mergeServiceFiles()
}

tasks.processResources {
    expand(
        "plugin_main_path" to "${project.group}.${project.name}",
        "plugin_version" to project.version,
        "plugin_name" to project.name
    )
}