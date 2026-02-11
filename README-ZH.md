# Build Variant Verify Plugin 🚀

![alt text](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
一个强大的 Android 构建辅助插件，旨在提升发布安全性和调试效率。它能在构建过程中自动打印变体信息，并强制校验最终生成的 APK/AAB 签名与原始 JKS 文件的一致性。
## ✨ 功能特性
🔍 签名一致性校验：自动提取 JKS 证书指纹并与生成的 APK/AAB 进行比对，防止误用签名。  
📦 全产物支持：完美支持 Android App Bundle (AAB) 和传统的 APK 文件。  
📊 构建信息汇总：实时打印当前变体的 Flavor、BuildType 以及自定义的 BuildConfig 字段。  
🛠️ 零配置接入：插拔式设计，无需修改原有复杂的构建逻辑。  

## 💻 快速接入
在你的项目根目录 build.gradle.kts 或 app/build.gradle.kts 中添加：  
```kotlin
plugins {
    id("io.github.OpenKing88.verify") version "1.0.0"
}
```

## 📖 使用示例
当你执行 ./gradlew assembleRelease 或 ./gradlew bundleRelease 时，插件会自动在 package 任务后执行。  
控制台输出预览

```kotlin
============================================================
  构建变体信息报告 [googleRelease]
============================================================
  [渠道名称]: google
  [BuildConfig 字段]:
    - BASE_URL = "https://api.example.com/"
    - VERSION_CODE = 1
============================================================

================================================================================
  签名一致性校验报告 [googleRelease]
  预期指纹 (JKS SHA-256): 7B:2A:91:FE:C2:58...
--------------------------------------------------------------------------------
  [APK]: app-google-release.apk
  [指纹]: 7B:2A:91:FE:C2:58...
  [结果]: ✅ 一致

  [AAB]: app-google-release.aab
  [指纹]: 7B:2A:91:FE:C2:58...
  [结果]: ✅ 一致
================================================================================
```

<img width="1041" height="624" alt="image" src="https://github.com/user-attachments/assets/b9679aaa-e443-4e62-9e9f-b4e786406d9b" />

<img width="959" height="525" alt="image" src="https://github.com/user-attachments/assets/183eb8ef-b088-4ea0-8fda-0cc684f7750a" />
  
## ⚙️ 进阶说明
### 自动挂载逻辑
该插件会自动挂载到以下 Gradle 任务之后：  
package[VariantName] (针对 APK)  
sign[VariantName]Bundle (针对 AAB)  
这意味着你不需要手动运行任何校验任务，它会作为构建流水线的一部分自动运行。  
## 🤝 贡献与反馈
如果你在使用过程中发现 Bug 或有功能建议，欢迎提交 Issue 或 Pull Request。  
