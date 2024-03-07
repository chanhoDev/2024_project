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

    }
}

rootProject.name = "alarmProject"
include(":app")
include(":alarm")
include(":common")
include(":calendar")
include(":imagerolling")
include(":Localization")
include(":graph")
include(":MPChartLib")
include(":motion")
include(":demo")
include(":demo2")
include(":demo_gyro")
include(":jetpackcompose")
include(":jetpackcompose:core:data")
