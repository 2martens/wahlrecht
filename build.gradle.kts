plugins {
    id("twomartens.versions")
    id("twomartens.nebula-release")
}

nebulaRelease {
    addReleaseBranchPattern("/main/")
}

versionCatalogUpdate {
    sortByKey.set(false)
    keep {
        keepUnusedVersions.set(true)
        keepUnusedLibraries.set(true)
        keepUnusedPlugins.set(true)
    }
}