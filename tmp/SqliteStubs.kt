package androidx.sqlite.db
interface SupportSQLiteQuery
class SimpleSQLiteQuery(val query: String, val args: Array<out Any?>) : SupportSQLiteQuery
