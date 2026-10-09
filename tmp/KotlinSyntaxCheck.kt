import java.io.File
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil

fun main(args: Array<String>) {
    val disposable = Disposer.newDisposable()
    try {
        val environment = KotlinCoreEnvironment.createForProduction(disposable, CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES)
        val factory = KtPsiFactory(environment.project)
        var errors = 0
        var checked = 0
        File(args[0]).walkTopDown().filter { it.extension == "kt" }.forEach { path ->
            checked++
            val file = factory.createFile(path.name, path.readText().replace("\r\n", "\n").removePrefix("\uFEFF"))
            PsiTreeUtil.collectElementsOfType(file, PsiErrorElement::class.java).forEach { error ->
                errors++
                println("${path.path}:${error.textRange.startOffset}: ${error.errorDescription}")
            }
        }
        check(errors == 0) { "$errors Kotlin syntax errors" }
        println("Kotlin syntax: $checked files checked")
    } finally { Disposer.dispose(disposable) }
}
