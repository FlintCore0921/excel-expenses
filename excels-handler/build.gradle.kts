import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "org.flintcore"
version = "0.0.1"

dependencies {
    compileOnly(platform(libs.excelib.bom))
    configureCompileOnlyExcelib(*ExcelibModules.values())
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}
