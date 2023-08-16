import org.gradle.accessors.dm.LibrariesForLibs
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

plugins {
    id("org.springframework.boot")
    id("twomartens.java")
}

val libs = the<LibrariesForLibs>()

dependencies {
    implementation(platform(libs.spring.boot))

    implementation(libs.bundles.spring.boot)
    testImplementation(libs.spring.boot.test)
}

sourceSets {
    create("integration-test") {
        java {
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            setSrcDirs(listOf("src/integration-test"))
        }
    }
}

idea {
    module {
        testSources.from(sourceSets["integration-test"].java.srcDirs)
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations {
    configureEach {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.register<Test>("integrationTest") {
    systemProperty("junit.jupiter.execution.parallel.enabled", true)
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
    useJUnitPlatform()
    maxHeapSize = "4g"
    group = "verification"
    workingDir = rootProject.projectDir
    testClassesDirs = sourceSets["integration-test"].output.classesDirs
    classpath = sourceSets["integration-test"].runtimeClasspath
}

tasks.named("buildAll") {
    dependsOn("integrationTest")
}

val formatter: DateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = rootProject.name
        attributes["Implementation-Version"] = archiveVersion.get()
        attributes["Implementation-Vendor"] = "Jim Martens"
        attributes["Build-Timestamp"] = ZonedDateTime.now().format(formatter)
        attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
        attributes["Build-Jdk"] = "${providers.systemProperty("java.version").get()} (${providers.systemProperty("java.vendor").get()} ${providers.systemProperty("java.vm.version").get()})"
        attributes["Build-OS"] = "${providers.systemProperty("os.name").get()} ${providers.systemProperty("os.arch").get()} ${providers.systemProperty("os.version").get()}"
    }
}

springBoot {
    buildInfo()
    mainClass.set("de.twomartens.wahlrecht.MainApplicationKt")
}
