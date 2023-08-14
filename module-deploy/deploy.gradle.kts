plugins {
    id("twomartens.jib")
}

dependencies {
    implementation(project(":server"))
}

val dockerUsername = System.getenv("DOCKER_USERNAME") ?: "UNKNOWN"
val dockerPassword = System.getenv("DOCKER_PASSWORD") ?: "UNKNOWN"

jib {
    from {
        image = "amazoncorretto:" + properties["projectSourceCompatibility"]
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "2martens/wahlrecht"
        tags = setOf(
                "latest",
                properties["version"].toString().replace("+", "-"))
        credHelper {
            helper = "pass"
        }
    }
    container {
        extraClasspath = listOf("/configmap")
        mainClass = "de.twomartens.wahlrecht.MainApplication"
        jvmFlags = listOf("-XX:+UseContainerSupport",
                "-XX:MaxRAMPercentage=75.0",
                "-Dlog4j.configurationFile=/configmap/log4j2.xml")
    }
}