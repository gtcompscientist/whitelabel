package co.csadev.whitelabel

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.AndroidSourceSet
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
                { "Type: '${example2Flavor.buildConfigFields["fieldName"]?.type}'" })
        assert(example2Flavor.buildConfigFields["fieldName"]?.value == "\"Config field here!\"",
                { "Value: '${example2Flavor.buildConfigFields["fieldName"]?.value}' != '\"Config field here!\"'" })
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
                { "Example: '${example2Flavor.manifestPlaceholders["placeholder_example"]}' != 'example'" })
        assert(example2Flavor.manifestPlaceholders["placeholder_test"] == "test",
                { "Example: '${example2Flavor.manifestPlaceholders["placeholder_test"]}' != 'test'" })
    }

    @Test
    fun `whitelabel flavors includes source folders that are present`() {
        val extension = evaluatableProject().extensions.findByType(AppExtension::class.java)!!
        testSourceSet(extension.sourceSets.findByName("example_1")!!, 1)
        testSourceSet(extension.sourceSets.findByName("example_2")!!, 2)
    }

    private fun testSourceSet(sourceSet: AndroidSourceSet, expectedSize: Int) {
        assert(sourceSet.renderscript.srcDirs.size == expectedSize)
        assert(sourceSet.aidl.srcDirs.size == expectedSize)
        assert(sourceSet.shaders.srcDirs.size == expectedSize)
        assert(sourceSet.assets.srcDirs.size == expectedSize)
        assert(sourceSet.java.srcDirs.size == expectedSize)
        assert(sourceSet.res.srcDirs.size == expectedSize)
        assert(sourceSet.jni.srcDirs.size == expectedSize)
        assert(sourceSet.jniLibs.srcDirs.size == expectedSize)
    }


    @Test
    fun `whitelabel config file adjusts options`() {
        val extension = configuredProject().extensions.findByType(AppExtension::class.java)!!
        //Should have two dimensions, and "whiteLabelconfiguredLabel" should be second
        assert(extension.flavorDimensionList.size == 2)
        assert(extension.flavorDimensionList[1].toLowerCase() == "whitelabelconfiguredlabel", { "Has Dimensions: ${extension.flavorDimensionList}" })

        //The folder is named differently, and can only be found if the config file is properly read
        testSourceSet(extension.sourceSets.findByName("example_1")!!, 1)
        testSourceSet(extension.sourceSets.findByName("example_2")!!, 2)

        //Configuration test turns off applicationIdSuffix
        val example1Flavor = extension.productFlavors.findByName("example_1")!!
        assert(example1Flavor.applicationIdSuffix.isNullOrEmpty(), {"Example 1 Suffix: ${example1Flavor.applicationIdSuffix}" })

        val example2Flavor = extension.productFlavors.findByName("example_2")!!
        assert(example2Flavor.applicationIdSuffix.isNullOrEmpty(), {"Example 2 Suffix: ${example2Flavor.applicationIdSuffix}" })
    }

    @Test
    fun `whitelabel config file allows multiple dimensions`() {
        val extension = multiDimenProject().extensions.findByType(AppExtension::class.java)!!
        //Should have three dimensions, and "whiteLabel" should be second
        assert(extension.flavorDimensionList.size == 3)
        assert(extension.flavorDimensionList[0].toLowerCase() == "whitelabelsecondarylabel", { "Has Dimensions: ${extension.flavorDimensionList}" })
        assert(extension.flavorDimensionList[1].toLowerCase() == "testflavor", { "Has Dimensions: ${extension.flavorDimensionList}" })
        assert(extension.flavorDimensionList[2].toLowerCase() == "whitelabelconfiguredlabel", { "Has Dimensions: ${extension.flavorDimensionList}" })

        //The folder is named differently, and can only be found if the config file is properly read
        testSourceSet(extension.sourceSets.findByName("example_1")!!, 1)
        testSourceSet(extension.sourceSets.findByName("example_2")!!, 2)
        testSourceSet(extension.sourceSets.findByName("secondary_1")!!, 1)
        testSourceSet(extension.sourceSets.findByName("secondary_2")!!, 2)

        //Configuration test turns off applicationIdSuffix
        val example1Flavor = extension.productFlavors.findByName("example_1")!!
        assert(example1Flavor.applicationIdSuffix.isNullOrEmpty(), {"Example 1 Suffix: ${example1Flavor.applicationIdSuffix}" })

        val example2Flavor = extension.productFlavors.findByName("example_2")!!
        assert(example2Flavor.applicationIdSuffix.isNullOrEmpty(), {"Example 2 Suffix: ${example2Flavor.applicationIdSuffix}" })

        //Secondary config includes applicationIdSuffix
        val secondary1Flavor = extension.productFlavors.findByName("secondary_1")!!
        assert(secondary1Flavor.applicationIdSuffix == "secondary_1", {"Secondary 1 Suffix: ${secondary1Flavor.applicationIdSuffix}" })

        val secondary2Flavor = extension.productFlavors.findByName("secondary_2")!!
        assert(secondary2Flavor.applicationIdSuffix == "differentExtension", {"Secondary 2 Suffix: ${secondary2Flavor.applicationIdSuffix}" })
    }

}