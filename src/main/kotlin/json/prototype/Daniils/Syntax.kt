package json.prototype.Daniils//package json.prototype
//
//import com.fasterxml.jackson.databind.JsonNode
//import kotlin.properties.ReadOnlyProperty
//import kotlin.reflect.KProperty
//
////public fun <T> lazyJson(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
//
//private const val PROPERTY_NAME = "PROPERTY_NAME"
//
///**
// * Когда
// */
//private const val PROPERTY_THIS = "PROPERTY_THIS"
//
//object JsonAdapterConfig {
//    var extractNullableFieldStrategy: JsonAdapterFieldExtractor =
//        JsonAdapterFieldExtractor { rootNode, targetFieldName -> rootNode.get(targetFieldName) }
////    var extractRequireFieldStrategy: JsonAdapterFieldExtractor =
////        JsonAdapterFieldExtractor { rootNode, targetFieldName ->
////            rootNode.get(targetFieldName)
////                ?: throw RuntimeException(
////                    "Required field \"$targetFieldName\" is not found in JsonNode with type \"${rootNode.nodeType.name}\" "
////                )
////        }
//}
//
//fun interface JsonAdapterFieldExtractor {
//    fun extract(rootNode: JsonNode, targetFieldName: String): JsonNode?
//}
//
//
///**
// * @param name имя Поля в JsonNode. Вложенные поля поддерживаются через поиск по дереву.
// * @param translator
// */
//fun interface JsonNodeMapper<T> : (JsonNode?) -> String
//
//
//interface JsonAdapter {
//    val json: JsonNode
//    companion object {
//        private fun err(msg: String): Nothing = throw RuntimeException(msg)
//        val textMapper = JsonNodeMapper<String> { node -> node?.asText() ?: err("") }
//    }
//
//    //    val richText get() =
//    val richText get() = JsonNodeMapper<String> { node -> node?.asText() ?: err("") }
//
//
//        val text: (JsonNode?) -> String get() { node -> node?.let { it.asText() } ?: "" }
//    val textNull: (JsonNode?) -> String get() = textMapper
//
//    //    val JsonNode?.text: JsonDelegate<String> get() = TODO()
//    val JsonNode?.asText: JsonDelegate<String> get() = TODO()
//    val JsonNode?.parseText: JsonDelegate<String> get() = TODO()
//
//
//    fun toText(node: JsonNode?): String = TODO()
//
//    infix fun JsonNode?.asText(name: String): JsonDelegate<String> = TODO("in progress")
//    infix fun <T> JsonNode.asText(translator: JsonNode.() -> T): JsonDelegate<T> = TODO("in progress")
//    public fun <T> JsonNode.asText(name: String = PROPERTY_NAME, translator: JsonNode.() -> T): Lazy<T> =
//        TODO("in progress")
//
//    public infix fun <T> JsonNode.asText(pair: Pair<String, JsonNode.() -> T>): Lazy<T> = TODO("in progress")
//
//    public fun <T> JsonNode.json(name: String = PROPERTY_NAME, translator: JsonNode.() -> T): Lazy<T> =
//        TODO("in progress")
//
//    public fun <T> JsonNode.uuid(translator: JsonNode.() -> T): Lazy<T> = TODO("in progress")
//    public fun <T> JsonNode.jsonSet(name: String = PROPERTY_NAME, eachElemMapper: JsonNode.() -> T): Lazy<Set<T>> =
//        TODO("in progress")
//
//    fun <T> JsonNode.field(name: String = "", translator: JsonNode.() -> T): Lazy<T> = TODO()
//
//
//    //    infix fun JsonNode.field(function: () -> Unit): ReadOnlyProperty<UserJsonAdapter, String> {
//    fun <T> asText(): JsonDelegate<T>? {
//
////        return JsonDelegate()
//        return null
//    }
//
//    class JsonDelegate<T>(
//        private val invokedJsonNode: JsonNode,
//        var fieldName: String,
//        private val fieldExtractor: JsonAdapterFieldExtractor,
//        private val jsonToValueConverter: (JsonNode?) -> T
//    ) : ReadOnlyProperty<JsonAdapter, T> {
//        private var _value: Any? = UNINITIALIZED_VALUE
//
//        @Suppress("UNCHECKED_CAST")
//        override operator fun getValue(thisRef: JsonAdapter, property: KProperty<*>): T {
//            if (_value !== UNINITIALIZED_VALUE) return _value as T
//            if (fieldName == PROPERTY_NAME) fieldName = property.name
//
//            try {
//                val field = fieldExtractor.extract(invokedJsonNode, fieldName)
//                return jsonToValueConverter.invoke(field)
//            } catch (e: Exception) {
//                throw RuntimeException(
//                    "Failed parse field \"$fieldName\" for Class \"${thisRef::class.qualifiedName}\" ", e
//                )
//            }
//        }
//
//        private object UNINITIALIZED_VALUE
//    }
//
//    infix fun <T> JsonDelegate<T>.from(newFieldName: String): JsonDelegate<T> = this.apply { fieldName = newFieldName }
//
//    //    infix fun <T> JsonNode.asType(mapper: (JsonNode?) -> T): JsonDelegate<T> = JsonDelegate()
//    infix fun <T> JsonNode.parse(mapper: (JsonNode?) -> T): JsonDelegate<T> = JsonDelegate(
//        this, PROPERTY_NAME, JsonAdapterConfig.extractNullableFieldStrategy, mapper
//    )
//}
//
//fun main() {
//    val j = JsonAdapter.textMapper
//    println(j::class.simpleName)
//    println(j::class.java)
//    println(j::class.javaObjectType)
//    println(j::class.javaPrimitiveType)
//    println(j::class.java.interfaces)
//    val o = UserJsonAdapter::class
////    println(o.simpleName)
//    println(o.qualifiedName)
////    println(o.jvmName)
//}