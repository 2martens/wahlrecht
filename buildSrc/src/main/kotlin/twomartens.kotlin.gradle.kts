import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("jvm")
}

val libs = the<LibrariesForLibs>()

dependencies {
    implementation(libs.kotlin.logging)
    implementation(kotlin("reflect"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}