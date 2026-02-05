package openking.plugins

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.serviceOf
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File

class BuildReportPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 1. 获取 ExecOperations 服务（这是处理过时警告的关键）
        val execOperations = project.serviceOf<ExecOperations>()

        val androidComponents =
            project.extensions.findByType(ApplicationAndroidComponentsExtension::class.java)
                ?: return
        val androidExtension = project.extensions.getByType(AppExtension::class.java)

        androidComponents.onVariants { variant ->
            val variantName = variant.name
            val capName = variantName.replaceFirstChar { it.uppercase() }

            // 注册打印信息任务
            val infoTask = project.tasks.register("printBuildInfo$capName") {
                group = "reporting"
                doLast {
                    println("\n" + "=".repeat(60))
                    println(" [渠道变体信息]: $variantName")
                    println(" [渠道信息]: ${variant.flavorName ?: "None"}")
                    println(" [配置参数信息]:")
                    variant.buildConfigFields?.get()?.forEach { (k, v) ->
                        println("    - $k = ${v.value}")
                    }
                    println("=".repeat(60))
                }
            }

            // 注册校验任务
            val verifyTask = project.tasks.register("verifyArtifactsSignature$capName") {
                group = "reporting"
                val apkDirProvider = variant.artifacts.get(SingleArtifact.APK)
                val aabFileProvider = variant.artifacts.get(SingleArtifact.BUNDLE)

                doLast {
                    val sc = getSigningConfig(variant, androidExtension)
                    val jksFile = sc?.storeFile
                    if (jksFile == null || !jksFile.exists()) {
                        println("[$variantName] 跳过校验：未找到签名文件配置")
                        return@doLast
                    }

                    // 使用 execOperations 代替 project.exec
                    val jksFingerprint = getFingerprint(
                        execOperations, "keytool", "-list", "-v",
                        "-keystore", jksFile.absolutePath,
                        "-alias", sc.keyAlias ?: "",
                        "-storepass", sc.storePassword ?: ""
                    )

                    println("\n" + "=".repeat(80))
                    println("  签名校验报告: $variantName")
                    println("  签名文件位置: ${jksFile.absolutePath}")
                    println("  签名文件别名: ${sc.keyAlias}")
                    println("  预期指纹 (JKS): $jksFingerprint")
                    println("-".repeat(80))

                    // 校验 APK
                    val apkFile = apkDirProvider.get().asFile.walkTopDown()
                        .firstOrNull { it.extension == "apk" }
                    verifyFile(execOperations, "APK", apkFile, jksFingerprint)

                    // 校验 AAB
                    val aabFile = aabFileProvider.orNull?.asFile
                    verifyFile(execOperations, "AAB", aabFile, jksFingerprint)

                    println("=".repeat(80) + "\n")
                }
            }

            project.tasks.matching {
                it.name == "package$capName" || it.name == "sign${capName}Bundle"
            }.configureEach {
                finalizedBy(verifyTask, infoTask)
            }
        }
    }

    private fun verifyFile(execOps: ExecOperations, type: String, file: File?, expected: String) {
        if (file == null || !file.exists()) {
            println("[$type]: 未生成或未找到文件")
            return
        }
        val actual = getFingerprint(execOps, "keytool", "-printcert", "-jarfile", file.absolutePath)
        val match = expected.equals(actual, ignoreCase = true)
        println("${type}安装包信息||")
        println("  [安装包位置]: ${file.absolutePath}")
        println("  [安装包指纹信息]: $actual")
        println("  [签名校验结果]: ${if (match) "✅ 一致" else "❌ 不匹配"}")
    }

    private fun getSigningConfig(
        variant: com.android.build.api.variant.Variant,
        android: AppExtension
    ) =
        android.productFlavors.findByName(variant.flavorName ?: "")?.signingConfig
            ?: android.buildTypes.findByName(variant.buildType ?: "")?.signingConfig
            ?: android.defaultConfig.signingConfig

    // 修改此函数，接收 ExecOperations 参数
    private fun getFingerprint(execOps: ExecOperations, vararg args: String): String {
        return try {
            val out = ByteArrayOutputStream()
            // 使用 execOps.exec 而不是 project.exec
            execOps.exec {
                commandLine(*args)
                standardOutput = out
            }.assertNormalExitValue()

            val output = out.toString()
            output.lines()
                .firstOrNull {
                    it.trim().startsWith("SHA256:", true) || it.trim().startsWith("SHA-256:", true)
                }
                ?.substringAfter(":")?.trim() ?: "未找到指纹"
        } catch (e: Exception) {
            "提取失败: ${e.message}"
        }
    }
}