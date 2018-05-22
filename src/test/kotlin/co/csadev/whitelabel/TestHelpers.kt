package co.csadev.whitelabel

import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import java.io.File


val FixtureWorkingDir = File("src/test/fixtures/app")
val EmptyWorkingDir = File("src/test/fixtures/empty_app")

fun fixtureProject(): Project {
    return ProjectBuilder.builder().withProjectDir(FixtureWorkingDir).build()
}

fun emptyProject(): Project {
    return ProjectBuilder.builder().withProjectDir(EmptyWorkingDir).build()
}

fun evaluatableProject(): Project {
    val project = fixtureProject()
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

    project.plugins.apply("co.csadev.whitelabel")
    return project
}

fun unusuableProject(): Project {
    val project = emptyProject()
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
    return project
}
