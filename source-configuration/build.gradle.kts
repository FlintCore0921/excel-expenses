plugins {
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.flintcore"
version = rootProject.version

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    compileOnly(project(":models"))
    compileOnly(libs.javafx.controls)
    compileOnly(libs.javafx.fxml)

    testCompileOnly(project(":models"))
    testCompileOnly(libs.javafx.controls)
    testCompileOnly(libs.javafx.fxml)
}

tasks.test {
    useJUnitPlatform()
}