import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  kotlin("jvm")
}

val libs = the<LibrariesForLibs>()

dependencies {
  implementation(libs.kotlin.logging)
  implementation(libs.kotlin.reactor)
  implementation(kotlin("reflect"))
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-Xjvm-default=all")
  }
}