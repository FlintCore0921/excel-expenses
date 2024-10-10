import org.gradle.kotlin.dsl.DependencyHandlerScope

const val excelibDependencyFormat = "org.flintcore.excelib:%s"

enum class ExcelibModules {
    COMMONS, FUNCTIONS, MODELS, SERVICES
}

fun DependencyHandlerScope.configureCompileOnlyExcelib(
    vararg modules: ExcelibModules,
) {
    modules.forEach {
        val dependencyNotation = excelibDependencyFormat.format(it.name.lowercase())
        add("compileOnly", dependencyNotation)
    }
}

fun DependencyHandlerScope.configureImplementationExcelib(
    vararg modules: ExcelibModules = ExcelibModules.values(),
) {
    modules.forEach {
        val dependencyNotation = excelibDependencyFormat.format(it.name.lowercase())
        add("implementation", dependencyNotation)
    }
}