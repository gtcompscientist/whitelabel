package co.csadev.whitelabel

import com.android.build.gradle.AppExtension
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.internal.impldep.org.testng.log4testng.Logger
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class WhiteLabelPluginTest {
    @Test(expected = PluginApplicationException::class)
    fun `applying to library project throws plugin exception`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.android.library")
        project.plugins.apply("co.csadev.whitelabel")
    }

    @Test(expected = PluginApplicationException::class)
    fun `plugin folder doesn't exist throws exception`() {
        unusuableProject().plugins.apply("co.csadev.whitelabel")
    }

    @Test
    fun `whitelabel folders create flavors`() {
        val extension = evaluatableProject().extensions.findByType(AppExtension::class.java)!!
        assert(extension.flavorDimensionList.size == 1)
        assert(extension.flavorDimensionList.contains("whiteLabel"), { "Has Dimensions: ${extension.flavorDimensionList}" })
        assert(extension.productFlavors.size == 2)
        assert(extension.productFlavors.findByName("example_1") != null)
        assert(extension.productFlavors.findByName("example_2") != null)
    }

    @Test
    fun `whitelabel flavors use applicationIdSuffix`() {
        val extension = evaluatableProject().extensions.findByType(AppExtension::class.java)!!
        val example1Flavor = extension.productFlavors.findByName("example_1")!!
        assert(example1Flavor.applicationIdSuffix == "example_1")

        val example2Flavor = extension.productFlavors.findByName("example_2")!!
        assert(example2Flavor.applicationIdSuffix == "differentExtension")
    }

    @Test
    fun `whitelabel flavors adds build config fields`() {
        val extension = evaluatableProject().extensions.findByType(AppExtension::class.java)!!
        val example1Flavor = extension.productFlavors.findByName("example_1")!!
        assert(example1Flavor.buildConfigFields.isEmpty())

        val example2Flavor = extension.productFlavors.findByName("example_2")!!
        assert(!example2Flavor.buildConfigFields.isEmpty())
        assert(example2Flavor.buildConfigFields.containsKey("fieldName"))
        assert(example2Flavor.buildConfigFields["fieldName"]?.type == "String",
                { "Type: ${example2Flavor.buildConfigFields["fieldName"]?.type}" })
        assert(example2Flavor.buildConfigFields["fieldName"]?.value == "\"Config field here!\"",
                { "Value: ${example2Flavor.buildConfigFields["fieldName"]?.value}" })
    }

    @Test
    fun `whitelabel flavors adds manifest placeholders`() {
        val extension = evaluatableProject().extensions.findByType(AppExtension::class.java)!!
        val example1Flavor = extension.productFlavors.findByName("example_1")!!
        assert(example1Flavor.manifestPlaceholders.isEmpty())

        val example2Flavor = extension.productFlavors.findByName("example_2")!!
        assert(!example2Flavor.manifestPlaceholders.isEmpty())
        example2Flavor.manifestPlaceholders["placeholder_example"]
        assert(example2Flavor.manifestPlaceholders.containsKey("placeholder_example"))
        assert(example2Flavor.manifestPlaceholders["placeholder_example"] == "example",
                { "Example: ${example2Flavor.manifestPlaceholders["placeholder_example"]}" })
        assert(example2Flavor.manifestPlaceholders["placeholder_test"] == "test",
                { "Example: ${example2Flavor.manifestPlaceholders["placeholder_test"]}" })
    }

    @Test
    fun `whitelabel flavors includes source folders that are present`() {
        val extension = evaluatableProject().extensions.findByType(AppExtension::class.java)!!
        val example1SourceSets = extension.sourceSets.findByName("example_1")!!
        assert(example1SourceSets.renderscript.srcDirs.size == 1)
        assert(example1SourceSets.aidl.srcDirs.size == 1)
        assert(example1SourceSets.shaders.srcDirs.size == 1)
        assert(example1SourceSets.assets.srcDirs.size == 1)
        assert(example1SourceSets.java.srcDirs.size == 1)
        assert(example1SourceSets.res.srcDirs.size == 1)
        assert(example1SourceSets.jni.srcDirs.size == 1)
        assert(example1SourceSets.jniLibs.srcDirs.size == 1)
        val example2SourceSets = extension.sourceSets.findByName("example_2")!!
        assert(example2SourceSets.renderscript.srcDirs.size == 2)
        assert(example2SourceSets.aidl.srcDirs.size == 2)
        assert(example2SourceSets.shaders.srcDirs.size == 2)
        assert(example2SourceSets.assets.srcDirs.size == 2)
        assert(example2SourceSets.java.srcDirs.size == 2)
        assert(example2SourceSets.res.srcDirs.size == 2)
        assert(example2SourceSets.jni.srcDirs.size == 2)
        assert(example2SourceSets.jniLibs.srcDirs.size == 2)
    }

}