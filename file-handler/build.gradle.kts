import java.util.Arrays

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
    // Own modules
    arrayOf(
        project(":models"),
        project(":managers"),
    ).forEach {
        compileOnly(it)
        testCompileOnly(it)
    }
    compileOnly(libs.javafx.controls)

    // BOM - Platforms
    arrayOf(
        platform(libs.excelib.bom)
    ).forEach {
        compileOnly(it)
        testCompileOnly(it)
    }
    configureCompileOnlyExcelib(*ExcelibModules.values())

    testCompileOnly(libs.javafx.controls)
}