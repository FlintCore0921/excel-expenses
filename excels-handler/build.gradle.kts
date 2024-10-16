import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "org.flintcore"
version = rootProject.version

dependencies {
    compileOnly(platform(libs.excelib.bom))
    configureCompileOnlyExcelib(*ExcelibModules.values())
}