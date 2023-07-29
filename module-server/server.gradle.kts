plugins {
    id("twomartens.spring-boot-cloud")
    id("twomartens.kotlin")
}

dependencies {
    implementation(libs.mapstruct.base)
    implementation(libs.bundles.spring.boot.security)
    annotationProcessor(libs.mapstruct.processor)
}