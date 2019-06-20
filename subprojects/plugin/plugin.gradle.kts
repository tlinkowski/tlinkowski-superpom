import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  //region MAIN
  kotlin("jvm") version "1.3.40"
  `java-gradle-plugin`
  `kotlin-dsl`
  //endregion

  //region TEST
  groovy
  //endregion
}

//region gradle.properties
val kordampVersion: String by project
val spockVersion: String by project
val groovyVersion: String by project
//endregion

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

repositories {
  gradlePluginPortal() // for other Gradle plugins
}

dependencies {
  //region MAIN
  implementation(kotlin("stdlib-jdk8"))
  implementation(group = "org.kordamp.gradle", name = "base-gradle-plugin", version = kordampVersion)
  //endregion

  //region TEST
  testImplementation(group = "org.spockframework", name = "spock-core", version = spockVersion)
  testImplementation(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVersion)
  //endregion
}

gradlePlugin {
  plugins {
    create("TLinkowskiSuperpom") {
      id = "pl.tlinkowski.tlinkowski-superpom"
      implementationClass = "pl.tlinkowski.TLinkowskiSuperpomPlugin"
    }
  }
}
