import org.gradle.accessors.dm.LibrariesForLibs
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    jacoco
    id("io.freefair.lombok")
    id("twomartens.java-base")
    id("twomartens.checkstyle")
}

val libs = the<LibrariesForLibs>()

dependencies {
    constraints.implementation(libs.bundles.logging)

    implementation(libs.slf4j.api)
    runtimeOnly(libs.bundles.logging)

    testImplementation(libs.bundles.test)
    testImplementation(kotlin("test-junit5"))
}

configurations {
    configureEach {
        exclude(group="junit", module="junit")
        // we are using log4j-slf4j2-impl, so we need to suppress spring include of log4j-slf4j-impl
        exclude(group="org.apache.logging.log4j", module="log4j-slf4j-impl")
    }
}

tasks.withType<Test>().configureEach {
    systemProperty("junit.jupiter.execution.parallel.enabled", true)
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    useJUnitPlatform()
    maxHeapSize = "4g"
    workingDir = rootProject.projectDir
    finalizedBy(tasks.jacocoTestReport)
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

tasks.jar {
    doFirst {
        manifest {
            attributes["Implementation-Title"] = rootProject.name
            attributes["Implementation-Version"] = archiveVersion.get()
            attributes["Implementation-Vendor"] = "Jim Martens"
            attributes["Build-Timestamp"] = LocalDateTime.now().format(formatter)
            attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
            attributes["Build-Jdk"] = "${providers.systemProperty("java.version").get()} (${providers.systemProperty("java.vendor").get()} ${providers.systemProperty("java.vm.version").get()})"
            attributes["Build-OS"] = "${providers.systemProperty("os.name").get()} ${providers.systemProperty("os.arch").get()} ${providers.systemProperty("os.version").get()}"
        }
    }
}

normalization.runtimeClasspath.metaInf {
    ignoreAttribute("Build-Timestamp")
}

tasks.register("cleanLibs") {
    delete("${buildDir}/libs")
}

tasks.build {
    dependsOn("cleanLibs")
}
