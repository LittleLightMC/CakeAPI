# CakeAPI
[![Build](https://github.com/LittleLightMC/CakeAPI/actions/workflows/build.yml/badge.svg)](https://github.com/LittleLightMC/CakeAPI/actions/workflows/build.yml)
[![JitPack](https://jitpack.io/v/LittleLightMC/CakeAPI.svg)](https://jitpack.io/#LittleLightMC/CakeAPI)

This is a Spigot plugin to reproduce 
Destiny in vanilla Minecraft server.

Now is WIP.

## Addons

You now can develop addon for CakeAPI.

### repo
Gradle
```kotlin
repositories {
    maven("https://jitpack.io")
}
dependencies {
    implementation("com.github.LittleLightMC:CakeAPI:-SNAPSHOT")
}
```

Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
<dependency>
    <groupId>com.github.LittleLightMC</groupId>
    <artifactId>CakeAPI</artifactId>
    <version>-SNAPSHOT</version>
</dependency>
</dependencies>
```

### Example code
Main class
```kotlin
class MyAddon: CakeAddon() {
    override fun init() {}
}
```
Like spigot plugin, you must provide a `addon.yml` at the root of your jar file.

e.g.
```yaml
name: test
version: 0.1.0
main: pro.darc.cake.addon.Test
id: af6bf111-d349-4d18-8433-eb926dae96c9
```

All fields above must provide.
- `id` is an uuid to identify your addon, you could generate it as you wish.
- `main` must not start with prefix `pro.darc.cake`
