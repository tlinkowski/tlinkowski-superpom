allprojects {
  repositories {
    mavenCentral()
  }
}

subprojects {
  group = "pl.tlinkowski"
}

apply(from = "$rootDir/shared.build.gradle.kts")
apply(from = "$rootDir/gradle/ide.gradle.kts")
