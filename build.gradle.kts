allprojects {
  repositories {
    mavenCentral()
  }
}

subprojects {
  group = "pl.tlinkowski"
}

apply(from = "$rootDir/gradle/ide.gradle.kts")
