import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.flintcore"
version = rootProject.version

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls")
}

dependencies {
    compileOnly(libs.javafx.controls)
    compileOnly(libs.javafx.fxml)
    testCompileOnly(libs.javafx.controls)
    testCompileOnly(libs.javafx.fxml)
}