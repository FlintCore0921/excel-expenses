group = "org.flintcore"
version = rootProject.version

dependencies {
    compileOnly(project(":models"))
    configureCompileOnlyExcelib(*ExcelibModules.values())

    testCompileOnly(project(":models"))
    testCompileOnly(platform(libs.excelib.bom))
    compileOnly(platform(libs.excelib.bom))
}