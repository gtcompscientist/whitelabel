package co.csadev.whitelabel

import com.android.build.gradle.AppExtension
import com.android.utils.FileUtils
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
        val extension = target.extensions.create("whiteLabel", WhiteLabelPluginExtension::class.java)
//        logger().log(LogLevel.INFO, "Configured WhiteLabel folder: ${extension.root?.path}")
//        logger().log(LogLevel.INFO, "Configured WhiteLabel dimension: ${extension.dimensionPosition}")
//        logger().log(LogLevel.INFO, "Configured WhiteLabel application suffix: ${extension.addApplicationSuffix}")

        val configFolder = extension.root ?: target.projectDir.listFiles()?.firstOrNull { f ->
            f.isDirectory && f.name.toLowerCase() == "whitelabel"
        } ?: throw IllegalArgumentException("Unable to find whitelabel directory.\nProject Directory:\t${target.projectDir}\nConfigured Root:${extension.root}")
        val subFolders = configFolder.listFiles().filter { it.isDirectory }

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
                        val segments = line
                                .split("\" ")
                                .filter { it.isNotEmpty() && it != "\"" }
                        val type = segments.getOrNull(0)
                                ?.removePrefix("\"")
                                ?.trim()
                                ?: continue
                        val name = segments.getOrNull(1)
                                ?.removePrefix("\"")
                                ?.trim()
                                ?.removeSuffix("\"")
                                ?: continue
                        val value = segments.getOrNull(2)
                                ?.removePrefix("\"")
                                ?.trim()
                                ?.removeSuffix("\"")
                                ?.replace("\\\"", "\"")
                                ?: continue
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
                        val segments = line
                                .split("\"")
                                .filter { it.isNotEmpty() && it != "\"" }
                        val key = segments.firstOrNull()
                                ?.trim()
                                ?.replaceFirst("\"", "")
                                ?.removeSuffix("\"")
                                ?: continue
                        val value = segments.lastOrNull()
                                ?.trim()
                                ?.replaceFirst("\"", "")
                                ?.removeSuffix("\"")
                                ?: continue
                        logger().log(LogLevel.INFO, "Adding Manifest Placeholder: '$key'->'$value'")
                        placeHolders[key] = value
                    }
                    flavor.addManifestPlaceholders(placeHolders)
                }
                val whiteLabelSource = WhiteLabelSourceFolders(white, logger())
                val sourceSet = android.sourceSets.maybeCreate(white.name)
                sourceSet.renderscript.setSrcDirs(whiteLabelSource.getRenderscriptDirectories(sourceSet.renderscript.srcDirs))
                sourceSet.aidl.setSrcDirs(whiteLabelSource.getAidlDirectories(sourceSet.aidl.srcDirs))
                sourceSet.shaders.setSrcDirs(whiteLabelSource.getShadersDirectories(sourceSet.shaders.srcDirs))
                sourceSet.assets.setSrcDirs(whiteLabelSource.getAssetsDirectories(sourceSet.assets.srcDirs))
                sourceSet.java.setSrcDirs(whiteLabelSource.getJavaDirectories(sourceSet.java.srcDirs))
                sourceSet.res.setSrcDirs(whiteLabelSource.getResDirectories(sourceSet.res.srcDirs))
                sourceSet.jni.setSrcDirs(whiteLabelSource.getJniDirectories(sourceSet.jni.srcDirs))
                sourceSet.jniLibs.setSrcDirs(whiteLabelSource.getJniLibsDirectories(sourceSet.jniLibs.srcDirs))
            })
        }
    }
}
