group = "org.flintcore"
version = rootProject.version

dependencies {
    compileOnly(project(":models"))
    testCompileOnly(project(":models"))

    compileOnly(platform(libs.excelib.bom))
    testCompileOnly(platform(libs.excelib.bom))
    configureCompileOnlyExcelib(*ExcelibModules.values())
}