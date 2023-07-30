import org.gradle.accessors.dm.LibrariesForLibs

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

springBoot {
    buildInfo()
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}

tasks.bootDistZip {
    dependsOn(tasks.jar)
}

tasks.bootDistTar {
    dependsOn(tasks.jar)
}

tasks.bootStartScripts {
    dependsOn(tasks.jar)
}