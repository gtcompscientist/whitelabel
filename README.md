# whitelabel
Gradle plugin for quickly creating white labeled apps without muddying your Android project folders

## Quick Start Guide

1. Include the Gradle plugin repository in `buildscript` `repositories` (see [Buildscript](#buildscript)).
1. Add plugin to `buildscript` `dependencies`.
1. Apply the plugin (see [Usage](#usage)).
1. Configure each specific white label flavor (see [Flavor Setup](#flavorsetup))

## Buildscript

Add to your buildscript dependencies (top-level build.gradle file):

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    // ... Other dependencies
    classpath "co.csadev.whitelabel:0.1"
  }
}
```

## Usage

Apply it to the bottom of your app-level build.gradle file as following below.

If you use the Google Services plugin, apply above that line

```groovy
apply plugin:'co.csadev.whitelabel'
apply plugin: 'com.google.gms.google-services'
```

The plugin doesn't explicitly create any tasks. It simply adds the flavors specified within your existing tasks.

## Configuration

Coming soon. Right now the ability to properly inject a flavor dimension and the white label flavors precludes normal configuration blocks. This is a TODO.

## Flavor Setup

Each flavor can have any or all of the following configuration files and folder:

```
- [app]
  |
  + - [whitelabel]
      |
      + - [flavorName]
          |
          + - [java]
          |
          + - [res]
          |
          + - [renderscript]
          |
          + - [aidl]
          |
          + - [shaders]
          |
          + - [jni]
          |
          + - [jniLibs]
          |
          + - buildConfig
          |
          + - manifestPlaceholders
          |
          + - applicationIdSuffix
```

Folders are all designed to be added to the sourceSets in the same way that you would [add them normally](https://developer.android.com/studio/build/build-variants#flavor-dimensions).

### `buildConfig`
Text file (no extension) to add BuildConfig fields as you would normally do within your app's `build.gradle`

Add one config per line, with the same order and escaping as you would normally include in your `build.gradle`, without comma separation.

Before:
```groovy
buildConfigField "String", "ConfigString", "\"ConfiguredFromGradleFlavor\""
```

After:
```
"String" "ConfigString" "\"ConfiguredFromGradleFlavor\""
```

### `manifestPlaceholders`
Text file (no extension) to include Manifest Placeholder fields as you would normally do within your app's `build.gradle`

Add one config per line, with the same order and escaping as you would normally include in your `build.gradle`, without comma separation.

Before:
```groovy
manifestPlaceholders.put("manifest_string", "configuredFromGradle")
```

After:
```
"manifest_string" "configuredFromGradle"
```

### `applicationIdSuffix`
Text file (no extension) to override the folder name as the applicationIdSuffix for the flavor

Only one line is read, with no quotes, or spaces allowed

Before:
```groovy
applicationIdSuffix ".flavorSuffix"
```

After:
```
flavorSuffix
```
