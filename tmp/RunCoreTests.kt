fun main() {
    var count = 0
    for (name in listOf("br.com.banavita.app.core.time.DashboardPeriodsTest", "br.com.banavita.app.core.time.RipeningClockTest")) {
        val clazz = Class.forName(name)
        for (method in clazz.declaredMethods) {
            val test = method.getAnnotation(org.junit.Test::class.java) ?: continue
            val exception = try { method.invoke(clazz.getDeclaredConstructor().newInstance()); null } catch (e: java.lang.reflect.InvocationTargetException) { e.cause }
            if (test.expected == org.junit.Test.None::class) check(exception == null) { "$name.${method.name}: $exception" }
            else check(exception != null && test.expected.java.isInstance(exception)) { "$name.${method.name}: expected ${test.expected}, got $exception" }
            count++
        }
    }
    println("Core Kotlin tests: $count passed (standalone assertion adapter; not Android/Gradle)")
}
