pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("$rootProject/maven") }
        mavenLocal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "FlowBusCore"
include(":app")
include(":FlowBusLib")
