package co.csadev.whitelabel

import com.android.build.gradle.AppExtension
import com.android.utils.FileUtils
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import java.io.File

class WhiteLabelPlugin : Plugin<Project> {
    companion object {
        private const val WhiteLabelDimension = "whiteLabel"
        private fun logger(): Logger {
            return Logging.getLogger("WhiteLabel")
        }
    }

    override fun apply(target: Project?) {
        val android = target?.extensions?.getByType(AppExtension::class.java) ?: throw IllegalStateException("The 'com.android.application' plugin is required.")
        val extension = target.extensions.create(WhiteLabelDimension, WhiteLabelPluginExtension::class.java)

        val jsonFile = target.projectDir.listFiles().firstOrNull { it.name.toLowerCase() == "whitelabel.json" }
        if (jsonFile != null) {
            try {
                val configText = JsonSlurper().parse(jsonFile) as LazyMap
                (configText.getOrDefault("root", null) as? String)?.let {
                    extension.root = File(it)
                }
                (configText.getOrDefault("dimensionPosition", null) as? Int)?.let {
                    extension.dimensionPosition = it
                }
                (configText.getOrDefault("addApplicationSuffix", null) as? Boolean)?.let {
                    extension.addApplicationSuffix = it
                }
            } catch (ex: Exception) {
                logger().log(LogLevel.INFO, "Unable to process config file:\n$ex")
                throw(ex)
            }
        }
        logger().log(LogLevel.INFO, "Configured WhiteLabel folder: ${extension.root?.path}")
        logger().log(LogLevel.INFO, "Configured WhiteLabel dimension: ${extension.dimensionPosition}")
        logger().log(LogLevel.INFO, "Configured WhiteLabel application suffix: ${extension.addApplicationSuffix}")
        val configFolder = extension.root ?: target.projectDir.listFiles()?.firstOrNull { f ->
            f.isDirectory && f.name.toLowerCase() == "whitelabel"
        } ?: throw IllegalArgumentException("Unable to find whitelabel directory.\nProject Directory:\t${target.projectDir}\nConfigured Root:${extension.root}")
        val subFolders = configFolder.listFiles()?.filter { it.isDirectory }
        if (subFolders?.isNotEmpty() != true)
            throw IllegalArgumentException("Unable to find whitelabel subfolders:\nConfig Path:${configFolder.absolutePath}")

        val flavorDimensionList = arrayListOf<String>()
        val flavorFolders = hashMapOf<String, File?>()
        android.flavorDimensionList?.let {
            if (it.size > 0)
                flavorDimensionList.addAll(it)
        }
        flavorDimensionList.add(extension.dimensionPosition, WhiteLabelDimension)
        android.flavorDimensions(*(flavorDimensionList.toTypedArray()))
        folders@ subFolders.forEach { white ->
            android.productFlavors.create(white.name, { flavor ->
                flavor.dimension = WhiteLabelDimension
                flavorFolders[flavor.name] = white
                FileUtils.find(white, "buildConfig").orNull()?.let { buildConfig ->
                    val lines = buildConfig.readLines()
                    for (line in lines) {
                        val firstSplit = line.indexOf(' ')
                        val secondSplit = line.indexOf(' ', firstSplit + 1)
                        val type = line.substring(0, firstSplit)
                        val name = line.substring(firstSplit + 1, secondSplit)
                        val value = line.substring(secondSplit + 1)
                        logger().log(LogLevel.INFO, "Adding Build Config: '$type'->'$name'->'$value'")
                        flavor.buildConfigField(type, name, value)
                    }
                }
                if (extension.addApplicationSuffix) {
                    var suffix = white.name
                    FileUtils.find(white, "applicationIdSuffix").orNull()?.let { buildConfig ->
                        val lines = buildConfig.readLines().firstOrNull() ?: return@let
                        suffix = lines.trim()
                    }
                    flavor.applicationIdSuffix = suffix
                }

                FileUtils.find(white, "manifestPlaceholders").orNull()?.let { buildConfig ->
                    val placeHolders = HashMap<String, Any>()
                    val lines = buildConfig.readLines()
                    for (line in lines) {
                        val split = line.indexOf(' ')
                        val key = line.substring(0, split)
                        var value = line.substring(split + 1)
                        if (value.startsWith("\""))
                            value = "\\" + value
                        if (value.endsWith("\""))
                            value = value.dropLast(1) + "\\\""
                        logger().log(LogLevel.INFO, "Adding Manifest Placeholder: '$key'->'$value'")
                        placeHolders[key] = value
                    }
                    flavor.addManifestPlaceholders(placeHolders)
                }
                val sourceSet = android.sourceSets.maybeCreate(white.name)
                sourceSet.renderscript.withExtraSource(white, WhiteLabelSourceFolders.renderscript, logger())
                sourceSet.aidl.withExtraSource(white, WhiteLabelSourceFolders.aidl, logger())
                sourceSet.shaders.withExtraSource(white, WhiteLabelSourceFolders.shaders, logger())
                sourceSet.assets.withExtraSource(white, WhiteLabelSourceFolders.assets, logger())
                sourceSet.java.withExtraSource(white, WhiteLabelSourceFolders.java, logger())
                sourceSet.res.withExtraSource(white, WhiteLabelSourceFolders.res, logger())
                sourceSet.jni.withExtraSource(white, WhiteLabelSourceFolders.jni, logger())
                sourceSet.jniLibs.withExtraSource(white, WhiteLabelSourceFolders.jniLibs, logger())
            })
        }
    }
}
