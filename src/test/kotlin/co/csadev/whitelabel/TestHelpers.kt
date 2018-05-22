package co.csadev.whitelabel

import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import java.io.File


val FixtureWorkingDir = File("src/test/fixtures/app")
val EmptyWorkingDir = File("src/test/fixtures/empty_app")
val ConfigWorkingDir = File("src/test/fixtures/config_app")

fun fixtureProject(): Project {
    return ProjectBuilder.builder().withProjectDir(FixtureWorkingDir).build()
}

fun emptyProject(): Project {
    return ProjectBuilder.builder().withProjectDir(EmptyWorkingDir).build()
}

fun configProject(): Project {
    return ProjectBuilder.builder().withProjectDir(ConfigWorkingDir).build()
}

fun evaluatableProject() = setupProject(fixtureProject(), true)
fun unusuableProject() = setupProject(emptyProject(), false)
fun configuredProject() = setupProject(configProject(), true, "testFlavor")

private fun setupProject(project: Project, applyPlugin: Boolean, additionalFlavorDimension: String? = null): Project {
    project.plugins.apply("com.android.application")
    val extension = project.extensions.findByType(AppExtension::class.java)!!
    extension.setCompileSdkVersion(27)
    extension.defaultConfig.apply {
        versionCode = 1
        versionName = "1.0"
        setMinSdkVersion(27)
        setTargetSdkVersion(27)
    }
    extension.buildTypes.maybeCreate("debug")
    extension.buildTypes.maybeCreate("release")

    additionalFlavorDimension?.let {
        val flavorDimensionList = arrayListOf(it)
        extension.flavorDimensions(*(flavorDimensionList.toTypedArray()))
    }

    if (applyPlugin)
        project.plugins.apply("co.csadev.whitelabel")

    return project
}
