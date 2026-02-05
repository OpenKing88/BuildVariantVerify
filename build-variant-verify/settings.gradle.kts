rootProject.name = "build-variant-verify"
include(":verify")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // 注意：这里的路径是相对于当前 settings.gradle.kts 的
            // 插件在主项目根目录下，所以主项目的 toml 在 ../gradle/libs.versions.toml
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
