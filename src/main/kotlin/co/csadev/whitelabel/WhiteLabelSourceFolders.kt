package co.csadev.whitelabel

import com.android.build.gradle.api.AndroidSourceDirectorySet
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import java.io.File

enum class WhiteLabelSourceFolders(val folder: String) {
    renderscript("renderscript"),
    aidl("aidl"),
    shaders("shaders"),
    assets("assets"),
    java("java"),
    res("res"),
    jni("jni"),
    jniLibs("jniLibs");

    fun directories(root: File, existing: Set<File>, logger: Logger?) = defaultDirectoriesOrEmpty(File(root, folder), existing, logger)

    private fun defaultDirectoriesOrEmpty(dir: File, existing: Set<File>, logger: Logger?): Iterable<*> {
        logger?.log(LogLevel.INFO, "Getting directory: ${dir.absolutePath}")
        if (!dir.exists() || !dir.isDirectory) return existing
        val newFiles = LinkedHashSet<File>()
        newFiles.add(dir)
        newFiles.addAll(existing)
        return newFiles
    }
}

fun AndroidSourceDirectorySet.withExtraSource(root: File, type: WhiteLabelSourceFolders, logger: Logger? = null) {
    setSrcDirs(type.directories(root, srcDirs, logger))
}