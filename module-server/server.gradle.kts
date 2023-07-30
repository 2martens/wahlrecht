plugins {
    id("twomartens.spring-boot-cloud")
    id("twomartens.kotlin")
    kotlin("kapt")
    alias(libs.plugins.kotlin.lombok)
}

dependencies {
    implementation(libs.mapstruct.base)
    implementation(libs.bundles.spring.boot.security)
    annotationProcessor(libs.mapstruct.processor)
    kapt(libs.mapstruct.processor)
}

kapt {
    keepJavacAnnotationProcessors = true
}