val projectname: String = providers.gradleProperty("projectname").get()
rootProject.name = projectname

include("server")
include("deploy")

for (subproject in rootProject.children) {
    subproject.projectDir = file("module-" + subproject.name)
    subproject.buildFileName = "${subproject.name}.gradle.kts"
}