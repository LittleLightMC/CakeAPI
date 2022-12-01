import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.5.21"
val spigotVersion = "1.17.1-R0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("kapt") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    `maven-publish`
}

group = "pro.darc.cake"
version = "0.1.28"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    google()
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
    maven("https://repo.viaversion.com")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")
    implementation("net.oneandone.reflections8:reflections8:0.11.7")
    implementation("org.javassist:javassist:3.28.0-GA")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.2.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    implementation("org.mongodb:mongodb-driver-reactivestreams:4.3.0")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.2.8")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
    implementation("com.github.LittleLightMC:Skedule:1.2.6")
    compileOnly("com.github.PlaceholderAPI:PlaceholderAPI:2.10.9")
    implementation("com.github.LittleLightMC:adventure-text-minimessage:708ef78731")
    implementation("com.github.LittleLightMC:adventure-platform:d218d21ef3")
    implementation("org.redisson:redisson:3.18.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi,kotlinx.coroutines.ExperimentalCoroutinesApi,kotlin.RequiresOptIn"
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

tasks.javadoc {
    options.encoding = "UTF-8"
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/LittleLightMC/CakeAPI")
            credentials {
                username = project.findProperty("llmc.usr") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("llmc.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = "cakeapi"
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
}
