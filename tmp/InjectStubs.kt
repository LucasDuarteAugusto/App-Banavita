package javax.inject
annotation class Inject
annotation class Singleton
fun interface Provider<T> { fun get(): T }
