package co.csadev.whitelabel

import org.junit.Test
import java.io.File

class WhiteLabelSourceFoldersTest {
    @Test
    fun `when folders don't exist use only defaults`() {
        val sourceFolders = WhiteLabelSourceFolders(EmptyWorkingDir)
        val defaultFile = File("src/test/fixtures/default")
        val singleFileSet = setOf(defaultFile)
        assert(sourceFolders.getRenderscriptDirectories(singleFileSet) == singleFileSet)
        assert(sourceFolders.getAidlDirectories(singleFileSet) == singleFileSet)
        assert(sourceFolders.getShadersDirectories(singleFileSet) == singleFileSet)
        assert(sourceFolders.getAssetsDirectories(singleFileSet) == singleFileSet)
        assert(sourceFolders.getJavaDirectories(singleFileSet) == singleFileSet)
        assert(sourceFolders.getResDirectories(singleFileSet) == singleFileSet)
        assert(sourceFolders.getJniDirectories(singleFileSet) == singleFileSet)
        assert(sourceFolders.getJniLibsDirectories(singleFileSet) == singleFileSet)
    }

    @Test
    fun `when folders exist prepend whitelabel folder`() {
        val sourceFolders = WhiteLabelSourceFolders(File("src/test/fixtures/app/src/main"))
        val defaultFile = File("src/test/fixtures/default")
        val singleFileSet = setOf(defaultFile)

        val renderscriptResult = sourceFolders.getRenderscriptDirectories(singleFileSet)
        assert(renderscriptResult != singleFileSet)
        assert(renderscriptResult.count() == 2)
        assert(renderscriptResult.first() != defaultFile)
        assert(renderscriptResult.last() == defaultFile)

        val aidlResult = sourceFolders.getAidlDirectories(singleFileSet)
        assert(aidlResult != singleFileSet)
        assert(aidlResult.count() == 2)
        assert(aidlResult.first() != defaultFile)
        assert(aidlResult.last() == defaultFile)

        val shadersResult = sourceFolders.getShadersDirectories(singleFileSet)
        assert(shadersResult != singleFileSet)
        assert(shadersResult.count() == 2)
        assert(shadersResult.first() != defaultFile)
        assert(shadersResult.last() == defaultFile)

        val assetsResult = sourceFolders.getAssetsDirectories(singleFileSet)
        assert(assetsResult != singleFileSet)
        assert(assetsResult.count() == 2)
        assert(assetsResult.first() != defaultFile)
        assert(assetsResult.last() == defaultFile)

        val javaResult = sourceFolders.getJavaDirectories(singleFileSet)
        assert(javaResult != singleFileSet)
        assert(javaResult.count() == 2)
        assert(javaResult.first() != defaultFile)
        assert(javaResult.last() == defaultFile)

        val resResult = sourceFolders.getResDirectories(singleFileSet)
        assert(resResult != singleFileSet)
        assert(resResult.count() == 2)
        assert(resResult.first() != defaultFile)
        assert(resResult.last() == defaultFile)

        val jniResult = sourceFolders.getJniDirectories(singleFileSet)
        assert(jniResult != singleFileSet)
        assert(jniResult.count() == 2)
        assert(jniResult.first() != defaultFile)
        assert(jniResult.last() == defaultFile)

        val jniLibsResult = sourceFolders.getJniLibsDirectories(singleFileSet)
        assert(jniLibsResult != singleFileSet)
        assert(jniLibsResult.count() == 2)
        assert(jniLibsResult.first() != defaultFile)
        assert(jniLibsResult.last() == defaultFile)
        assert(jniLibsResult.last() == defaultFile)

    }
}