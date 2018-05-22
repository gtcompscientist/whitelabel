package co.csadev.whitelabel

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import java.io.File

class WhiteLabelSourceFolders(private val rootDir: File, private val logger: Logger? = null) {

    private fun defaultDirectoriesOrEmpty(dir: File, existing: Set<File>): Iterable<*> {
        logger?.log(LogLevel.INFO, "Getting directory: ${dir.path}")
        if (!dir.exists() || !dir.isDirectory) return existing
        val newFiles = LinkedHashSet<File>()
        newFiles.add(dir)
        newFiles.addAll(existing)
        return newFiles
    }

    fun getRenderscriptDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "renderscript"), existing)
    }

    fun getAidlDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "aidl"), existing)
    }

    fun getShadersDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "shaders"), existing)
    }

    fun getAssetsDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "assets"), existing)
    }

    fun getJavaDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "java"), existing)
    }

    fun getResDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "res"), existing)
    }

    fun getJniDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "jni"), existing)
    }

    fun getJniLibsDirectories(existing: Set<File>): Iterable<*> {
        return defaultDirectoriesOrEmpty(File(rootDir, "jniLibs"), existing)
    }
}