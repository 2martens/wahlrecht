import java.nio.file.Files

plugins {
    id("twomartens.base")
}

apply(plugin="com.netflix.nebula.release")

tasks.register("writeVersionProperties") {
    group = "version"
    mustRunAfter("release")
    outputs.file("${layout.buildDirectory}/version.properties")
    val directory = layout.buildDirectory.asFile
    doLast {
        Files.createDirectories(directory.get().toPath())
        File("${layout.buildDirectory}/version.properties").writeText("VERSION=${project.version}\n")
    }
}