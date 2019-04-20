import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.30"
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
dependencies {
  implementation(kotlin("stdlib-jdk8"))
}
