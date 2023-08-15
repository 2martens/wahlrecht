import java.nio.file.Files

plugins {
    id("twomartens.base")
}

apply(plugin="com.netflix.nebula.release")

tasks.register("writeVersionProperties") {
    group = "version"
    mustRunAfter("release")
    outputs.file("$buildDir/version.properties")
    val directory = buildDir
    doLast {
        Files.createDirectories(directory.toPath())
        File("$buildDir/version.properties").writeText("VERSION=${project.version}\n")
    }
}