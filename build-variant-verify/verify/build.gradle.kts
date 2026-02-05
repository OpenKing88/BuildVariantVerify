plugins {
    `kotlin-dsl`
    `maven-publish` // 必须，发布的基础
    id("com.gradle.plugin-publish") version "1.2.1" // Gradle 官方发布插件
}

group = "openking.plugins"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    // 这里的版本建议与你项目根目录使用的 AGP 版本一致
    compileOnly(libs.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    // 插件官网（通常放 GitHub 地址）
    website.set("https://github.com/OpenKing88/BuildVariantVerify")
    // 源码仓库地址
    vcsUrl.set("https://github.com/OpenKing88/BuildVariantVerify.git")
    plugins {
        create("buildVerify") {
            id = "openking.plugins.verify"
            displayName = "Android Build Variant Verifier" // 插件市场显示的标题
            description = "A plugin to verify APK/AAB signature and print build config info." // 详细描述
            tags.set(listOf("android", "build", "signature", "verify", "aab", "apk")) // 搜索标签
            implementationClass = "openking.plugins.BuildReportPlugin"
        }
    }
}