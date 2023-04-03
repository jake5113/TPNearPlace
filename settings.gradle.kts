pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // kakao sdk repository
        maven ( url = "https://devrepo.kakao.com/nexus/content/groups/public/" )
    }
}
rootProject.name = "TPKaoSearchApp"
include(":app")
