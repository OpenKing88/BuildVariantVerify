# Build Variant Verify Plugin 🚀

![alt text](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
A powerful Android build helper plugin designed to enhance release security and debugging efficiency. It automatically prints variant information during the build process and enforces verification that the final generated APK/AAB signatures match the original JKS file.
## ✨ Features
🔍 Signature Consistency Verification: Automatically extracts JKS certificate fingerprints and compares them with the generated APK/AAB to prevent misuse of signatures.  
📦 Full Product Support: Fully supports Android App Bundle (AAB) and traditional APK files.  
📊 Build Information Summary: Real-time printing of the current variant's Flavor, BuildType, and custom BuildConfig fields.  
🛠️ Zero-configuration integration: Plug-and-play design requires no modification to existing complex build logic.

## 💻 Quick Start
Add the following to your project's root build.gradle.kts or app/build.gradle.kts file:  
```kotlin
plugins {
    id("io.github.OpenKing88.verify") version "1.0.0"
}
```

## 📖 Usage Example
When you run ./gradlew assembleRelease or ./gradlew bundleRelease, the plugin automatically executes after the package task.  
Console output preview

```kotlin
============================================================
Establish Variant Information Reporting [googleRelease]
============================================================
  [Channel Name]: google
  [BuildConfig Fields]:
    - BASE_URL = "https://api.example.com/"
    - VERSION_CODE = 1
============================================================

================================================================================
  Signature Consistency Verification Report [googleRelease]
  Expected Fingerprint (JKS SHA-256): 7B:2A:91:FE:C2:58...
--------------------------------------------------------------------------------
  [APK]: app-google-release.apk
  [Fingerprint]: 7B:2A:91:FE:C2:58...
  [Result]: ✅ Unanimous

  [AAB]: app-google-release.aab
  [Fingerprint]: 7B:2A:91:FE:C2:58...
  [Result]: ✅ Unanimous
================================================================================
```

<img width="1041" height="624" alt="image" src="https://github.com/user-attachments/assets/b9679aaa-e443-4e62-9e9f-b4e786406d9b" />

<img width="959" height="525" alt="image" src="https://github.com/user-attachments/assets/183eb8ef-b088-4ea0-8fda-0cc684f7750a" />
  
## ⚙️ Advanced Notes
### Auto-mount Logic
This plugin will automatically attach itself after the following Gradle tasks:  
package[VariantName] (For APK)  
sign[VariantName]Bundle (For AAB)  
This means you don't need to manually run any verification tasks; they will run automatically as part of the build pipeline.
## 🤝 Contributions and Feedback
If you encounter any bugs or have feature suggestions during use, feel free to submit an issue or pull request.  
