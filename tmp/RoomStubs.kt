package androidx.room
import kotlin.reflect.KClass
annotation class Dao
annotation class Query(val value: String)
annotation class RawQuery(val observedEntities: Array<KClass<*>> = [])
annotation class Insert(val onConflict: Int = 0)
annotation class Update
annotation class Delete
annotation class Transaction
annotation class Entity(val tableName: String = "", val primaryKeys: Array<String> = [], val foreignKeys: Array<ForeignKey> = [], val indices: Array<Index> = [])
annotation class ForeignKey(val entity: KClass<*>, val parentColumns: Array<String>, val childColumns: Array<String>, val onDelete: Int = 1, val onUpdate: Int = 1) {
    companion object { const val RESTRICT = 2; const val CASCADE = 5; const val SET_NULL = 3; const val NO_ACTION = 1 }
}
annotation class Index(vararg val value: String, val unique: Boolean = false)
annotation class PrimaryKey
annotation class ColumnInfo(val name: String = "", val defaultValue: String = "")
annotation class DatabaseView(val value: String = "", val viewName: String = "")
annotation class Database(val entities: Array<KClass<*>>, val views: Array<KClass<*>> = [], val version: Int, val exportSchema: Boolean = false)
annotation class TypeConverters(vararg val value: KClass<*>)
annotation class TypeConverter
annotation class Embedded
annotation class Relation(val parentColumn: String, val entityColumn: String)
object OnConflictStrategy { const val ABORT = 3; const val REPLACE = 1 }
open class RoomDatabase
suspend fun <T> RoomDatabase.withTransaction(block: suspend () -> T): T = block()
