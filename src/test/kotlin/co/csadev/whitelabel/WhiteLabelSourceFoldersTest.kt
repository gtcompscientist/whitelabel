package co.csadev.whitelabel

import com.android.build.gradle.AppExtension
import org.junit.Test
import java.io.File

class WhiteLabelSourceFoldersTest {

    @Test
    fun `when folders don't exist use only defaults`() {
        val extension = unappliedProject().extensions.findByType(AppExtension::class.java)!!
        assert(extension.sourceSets.size == 8, { "Expected '8' sourceSet, got '${extension.sourceSets.size}'" })

        val test = extension.sourceSets.maybeCreate("test")
        assert(test.renderscript.srcDirs.size == 1, { "Expected '1' renderscript srcDir, got '${test.renderscript.srcDirs.size}'" })
        test.renderscript.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.renderscript)
        assert(test.renderscript.srcDirs.size == 1, { "Expected '1' renderscript srcDir, got '${test.renderscript.srcDirs.size}'" })

        assert(test.aidl.srcDirs.size == 1, { "Expected '1' aidl srcDir, got '${test.aidl.srcDirs.size}'" })
        test.aidl.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.aidl)
        assert(test.aidl.srcDirs.size == 1, { "Expected '1' aidl srcDir, got '${test.aidl.srcDirs.size}'" })

        assert(test.shaders.srcDirs.size == 1, { "Expected '1' shaders srcDir, got '${test.shaders.srcDirs.size}'" })
        test.shaders.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.shaders)
        assert(test.shaders.srcDirs.size == 1, { "Expected '1' shaders srcDir, got '${test.shaders.srcDirs.size}'" })

        assert(test.assets.srcDirs.size == 1, { "Expected '1' assets srcDir, got '${test.assets.srcDirs.size}'" })
        test.assets.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.assets)
        assert(test.assets.srcDirs.size == 1, { "Expected '1' assets srcDir, got '${test.assets.srcDirs.size}'" })

        assert(test.java.srcDirs.size == 1, { "Expected '1' java srcDir, got '${test.java.srcDirs.size}'" })
        test.java.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.java)
        assert(test.java.srcDirs.size == 1, { "Expected '1' java srcDir, got '${test.java.srcDirs.size}'" })

        assert(test.res.srcDirs.size == 1, { "Expected '1' res srcDir, got '${test.res.srcDirs.size}'" })
        test.res.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.res)
        assert(test.res.srcDirs.size == 1, { "Expected '1' res srcDir, got '${test.res.srcDirs.size}'" })

        assert(test.jni.srcDirs.size == 1, { "Expected '1' jni srcDir, got '${test.jni.srcDirs.size}'" })
        test.jni.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.jni)
        assert(test.jni.srcDirs.size == 1, { "Expected '1' jni srcDir, got '${test.jni.srcDirs.size}'" })

        assert(test.jniLibs.srcDirs.size == 1, { "Expected '1' jniLibs srcDir, got '${test.jniLibs.srcDirs.size}'" })
        test.jniLibs.withExtraSource(EmptyWorkingDir, WhiteLabelSourceFolders.jniLibs)
        assert(test.jniLibs.srcDirs.size == 1, { "Expected '1' jniLibs srcDir, got '${test.jniLibs.srcDirs.size}'" })    }

    @Test
    fun `when folders exist prepend whitelabel folder`() {
        val extension = evaluatableProject().extensions.findByType(AppExtension::class.java)!!
        assert(extension.sourceSets.size == 14, { "Expected '14' sourceSet, got '${extension.sourceSets.size}'" })
        val noExtraSet = extension.sourceSets.maybeCreate("example_1")
        assert(noExtraSet.renderscript.srcDirs.size == 1, { "Expected '1' renderscript srcDir, got '${noExtraSet.renderscript.srcDirs.size}'" })
        assert(noExtraSet.aidl.srcDirs.size == 1, { "Expected '1' aidl srcDir, got '${noExtraSet.aidl.srcDirs.size}'" })
        assert(noExtraSet.shaders.srcDirs.size == 1, { "Expected '1' shaders srcDir, got '${noExtraSet.shaders.srcDirs.size}'" })
        assert(noExtraSet.assets.srcDirs.size == 1, { "Expected '1' assets srcDir, got '${noExtraSet.assets.srcDirs.size}'" })
        assert(noExtraSet.java.srcDirs.size == 1, { "Expected '1' java srcDir, got '${noExtraSet.java.srcDirs.size}'" })
        assert(noExtraSet.res.srcDirs.size == 1, { "Expected '1' res srcDir, got '${noExtraSet.res.srcDirs.size}'" })
        assert(noExtraSet.jni.srcDirs.size == 1, { "Expected '1' jni srcDir, got '${noExtraSet.jni.srcDirs.size}'" })
        assert(noExtraSet.jniLibs.srcDirs.size == 1, { "Expected '1' jniLibs srcDir, got '${noExtraSet.jniLibs.srcDirs.size}'" })

        val example2Path = File(FixtureWorkingDir, "whitelabel/example_2").absolutePath

        val hasExtraSet = extension.sourceSets.maybeCreate("example_2")
        assert(hasExtraSet.renderscript.srcDirs.size == 2, { "Expected '2' renderscript srcDir, got '${hasExtraSet.renderscript.srcDirs.size}'" })
        assert(hasExtraSet.renderscript.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.renderscript.srcDirs.last().parentFile.absolutePath != example2Path)
        assert(hasExtraSet.aidl.srcDirs.size == 2, { "Expected '2' aidl srcDir, got '${hasExtraSet.aidl.srcDirs.size}'" })
        assert(hasExtraSet.aidl.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.aidl.srcDirs.last().parentFile.absolutePath != example2Path)
        assert(hasExtraSet.shaders.srcDirs.size == 2, { "Expected '2' shaders srcDir, got '${hasExtraSet.shaders.srcDirs.size}'" })
        assert(hasExtraSet.shaders.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.shaders.srcDirs.last().parentFile.absolutePath != example2Path)
        assert(hasExtraSet.assets.srcDirs.size == 2, { "Expected '2' assets srcDir, got '${hasExtraSet.assets.srcDirs.size}'" })
        assert(hasExtraSet.assets.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.assets.srcDirs.last().parentFile.absolutePath != example2Path)
        assert(hasExtraSet.java.srcDirs.size == 2, { "Expected '2' java srcDir, got '${hasExtraSet.java.srcDirs.size}'" })
        assert(hasExtraSet.java.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.java.srcDirs.last().parentFile.absolutePath != example2Path)
        assert(hasExtraSet.res.srcDirs.size == 2, { "Expected '2' res srcDir, got '${hasExtraSet.res.srcDirs.size}'" })
        assert(hasExtraSet.res.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.res.srcDirs.last().parentFile.absolutePath != example2Path)
        assert(hasExtraSet.jni.srcDirs.size == 2, { "Expected '2' jni srcDir, got '${hasExtraSet.jni.srcDirs.size}'" })
        assert(hasExtraSet.jni.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.jni.srcDirs.last().parentFile.absolutePath != example2Path)
        assert(hasExtraSet.jniLibs.srcDirs.size == 2, { "Expected '2' jniLibs srcDir, got '${hasExtraSet.jniLibs.srcDirs.size}'" })
        assert(hasExtraSet.jniLibs.srcDirs.first().parentFile.absolutePath == example2Path)
        assert(hasExtraSet.jniLibs.srcDirs.last().parentFile.absolutePath != example2Path)
    }
}