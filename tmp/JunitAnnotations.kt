package org.junit
import kotlin.reflect.KClass
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Test(val expected: KClass<out Throwable> = None::class) {
    class None : Throwable()
}
