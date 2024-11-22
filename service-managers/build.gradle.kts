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
    compileOnly(project(":managers"))
    compileOnly(project(":file-handler"))
    compileOnly(project(":excels-handler"))
    compileOnly(libs.javafx.controls)
    compileOnly(libs.javafx.fxml)

    compileOnly(platform(libs.excelib.bom))
    testCompileOnly(platform(libs.excelib.bom))
    configureCompileOnlyExcelib(*ExcelibModules.values())

    testCompileOnly(project(":models"))
    testCompileOnly(project(":managers"))
    testCompileOnly(project(":file-handler"))
    testCompileOnly(project(":excels-handler"))
    testCompileOnly(libs.javafx.controls)
    testCompileOnly(libs.javafx.fxml)
}