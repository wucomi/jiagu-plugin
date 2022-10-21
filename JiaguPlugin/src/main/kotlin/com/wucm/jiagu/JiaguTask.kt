package com.wucm.jiagu

import com.android.builder.model.SigningConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class JiaguTask : DefaultTask {
    private val inputFile: File
    private val jiaguExtension: JiaguExtension
    private val signingConfig: SigningConfig?
    private val jarPath: String

    @Inject
    constructor(
        inputFile: File,
        jiaguExtension: JiaguExtension,
        signingConfig: SigningConfig?
    ) : super() {
        this.inputFile = inputFile
        this.jiaguExtension = jiaguExtension
        this.signingConfig = signingConfig

        this.jarPath = jiaguExtension.installDir + "/jiagu/jiagu.jar"

        //设置分组，不设置分组默认发在other
        group = "jiagu"
    }

    @TaskAction
    fun jiagu() {
        //执行登录命令
        project.exec {
            //-login <username> <password>
            it.commandLine(
                "java",
                "-jar",
                jarPath,
                "-login",
                jiaguExtension.userName,
                jiaguExtension.userPassword,
            )
        }

        //导入签名信息
        project.exec {
            //-importsign <keystore_path> <keystore_password> <alias> <alias_password>
            it.commandLine(
                "java",
                "-jar",
                jarPath,
                "-importsign",
                signingConfig?.storeFile?.canonicalPath,
                signingConfig?.storePassword,
                signingConfig?.keyAlias,
                signingConfig?.keyPassword,
            )
        }

        //执行加固命令
        project.exec {
            //-jiagu <inputAPKpath> <outputPath> [-autosign]
            it.commandLine(
                "java",
                "-jar",
                jarPath,
                "-jiagu",
                inputFile.canonicalPath,
                inputFile.parent,
                "-autosign"
            )
        }
    }
}