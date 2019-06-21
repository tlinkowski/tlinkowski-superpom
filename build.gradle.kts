allprojects {
  repositories {
    mavenCentral()
  }
}

apply(from = "$rootDir/shared.build.gradle.kts")
apply(from = "$rootDir/gradle/ide.gradle.kts")
