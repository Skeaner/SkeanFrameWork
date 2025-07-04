pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url=uri("https://jitpack.io") }
        maven { url = uri("https://gitee.com/skean/MavenStore/raw/main/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://nexus.terrestris.de/repository/public/") }
    }
    // todo 删掉这段和对应的文件
    versionCatalogs {
        create("fwlibs") {
            from(files("gradle/fwlibs.versions.toml"))
        }
    }
}

include(":app",":SkeanFrameWork")
rootProject.name="SkeanFrameWork"

