package com.wucm.jiagu

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.cxx.logging.infoln
import com.android.build.gradle.internal.cxx.logging.warnln
import com.android.builder.model.SigningConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class JiaguPlugin : Plugin<Project> {
    private var jiaguExtensions: JiaguExtension? = null
    override fun apply(project: Project) {
        jiaguExtensions = project.extensions.create("jiagu", JiaguExtension::class.java)

        project.afterEvaluate {
            val appExtension = project.extensions.findByType(AppExtension::class.java)
            //获取变体
            appExtension?.applicationVariants?.all { variant ->
                variant.outputs.all { output ->
                    //输出的文件
                    val outputFile = output.outputFile
                    createJiaguTask(
                        project,
                        "jiagu" + variant.name,
                        outputFile,
                        variant.signingConfig
                    )
                }
            }
        }
    }

    private fun createJiaguTask(
        project: Project,
        taskName: String,
        outputFile: File,
        signingConfig: SigningConfig?,
    ) {
        if (!outputFile.isFile || !outputFile.name.endsWith(".apk")) {
            return
        }
        warnln("获取到变体的APK输出目录：" + outputFile.absolutePath)
        //创建加固Task
        project.tasks.create(
            taskName,
            JiaguTask::class.java,
            outputFile,
            jiaguExtensions,
            signingConfig
        )
    }
}