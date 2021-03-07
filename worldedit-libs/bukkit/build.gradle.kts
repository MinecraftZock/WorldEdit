applyLibrariesConfiguration()
constrainDependenciesToLibsCore()

repositories {
    maven {
        name = "SpigotMC"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    "shade"("net.kyori:text-adapter-bukkit:${Versions.TEXT_EXTRAS}")
}
tasks.named<Jar>("jar") {
    val projectVersion = project.properties["gitVersion"]
    version = projectVersion.toString()
}
