plugins {
  id("twomartens.spring-boot-cloud")
}

dependencies {
  implementation(libs.mapstruct.base)
  implementation(libs.bundles.spring.boot.security)
  annotationProcessor(libs.mapstruct.processor)
}