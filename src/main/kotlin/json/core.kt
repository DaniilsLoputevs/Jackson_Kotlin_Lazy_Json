package json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import json.JsonAdapter.Companion.err
import json.JsonAdapter.Companion.mapIfNotNull
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RootIsNull(targetFieldName: String) :
    NullPointerException("NPE: Root is null! (null as JsonNode).get(\"$targetFieldName\")")

class JsonLazyParseException(msg: String, e: Exception) : RuntimeException(msg, e)

/**
 *  rootNode: JsonNode - Корневой узел в котором нужно искать поле.
 *  targetFieldName: JsonNode - имя поля которое ищем.
 */
fun interface JsonAdapterFieldExtractor : (JsonNode?, String) -> JsonNode?

val defaultExtractor = JsonAdapterFieldExtractor { rootNode, targetFieldName ->
    if (rootNode == null) throw RootIsNull(targetFieldName)
    else rootNode.get(targetFieldName)
}

/**
 * node : JsonNode - Конвертируемый узел.
 * result : E -  результат конвертации.
 */
fun interface JsonNodeMapper<E> : (JsonNode?) -> E


interface JsonAdapter {
    val json: JsonNode


    val int: (JsonNode?) -> Int get() = intMapper
    val long: (JsonNode?) -> Long get() = longMapper
    val double: (JsonNode?) -> Double get() = doubleMapper
    val boolean: (JsonNode?) -> Boolean get() = booleanMapper

    /**
     * Если Узел не был найден - throw RuntimeException
     * Если Узел найден -> String.valueOf(node.value) // что приводит к [null -> "null", 123 -> "123", true -> "true" и т.д.
     */
    val text: (JsonNode?) -> String get() = textMapper

    /**
     * Если Узел не был найден - return null
     * Если Узел найден -> String.valueOf(node.value) // что приводит к [null -> "null", 123 -> "123", true -> "true" и т.д.
     */
    val textNull: (JsonNode?) -> String? get() = textNullMapper

    /**
     * Если Узел не был найден - return ""
     * Если Узел найден -> String.valueOf(node.value) // что приводит к [null -> "null", 123 -> "123", true -> "true" и т.д.
     */
    val textEmpty: (JsonNode?) -> String get() = textEmptyMapper

    val uuid: (JsonNode?) -> UUID get() = uuidMapper
    val uuidNull: (JsonNode?) -> UUID? get() = uuidNullMapper
    val bigInteger: (JsonNode?) -> BigInteger get() = bigIntegerMapper
    val bigIntegerNull: (JsonNode?) -> BigInteger? get() = bigIntegerNullMapper
    val bigDecimal: (JsonNode?) -> BigDecimal get() = bigDecimalMapper
    val bigDecimalNull: (JsonNode?) -> BigDecimal? get() = bigDecimalNullMapper
    val localDate: (JsonNode?) -> LocalDate get() = localDateMapper
    val localDateNull: (JsonNode?) -> LocalDate? get() = localDateNullMapper
    val offsetDateTime: (JsonNode?) -> OffsetDateTime get() = offsetDateTimeMapper
    val offsetDateTimeNull: (JsonNode?) -> OffsetDateTime? get() = offsetDateTimeNullMapper

    /*
    **********************************************************
    * Immutable Collections
    **********************************************************
     */

    /** Если Узел не найден или коллекция пуста -> return emptyList<K,V>() */
    infix fun <E> list(elemMapper: (JsonNode) -> E): JsonNodeMapper<List<E>> =
        JsonNodeMapper { node ->
            node.mapIfNotNull { elements()!!.asSequence().map(elemMapper).toList() } ?: emptyList()
        }

    /** Если Узел не найден или коллекция пуста -> return emptySet<K,V>() */
    infix fun <E> set(elemMapper: (JsonNode) -> E): JsonNodeMapper<Set<E>> =
        JsonNodeMapper { node -> node.mapIfNotNull { elements()!!.asSequence().map(elemMapper).toSet() } ?: emptySet() }

    /** Если Узел не найден или коллекция пуста -> return emptyMap<K,V>() */
    infix fun <K, V> map(keyValueMapper: (String, JsonNode) -> Pair<K, V>): JsonNodeMapper<Map<K, V>> =
        JsonNodeMapper { node ->
            node.mapIfNotNull {
                fields().asSequence().associate { (key, value) -> keyValueMapper.invoke(key, value) }
            } ?: emptyMap()
        }

    /** Если Узел не найден или коллекция пуста -> return emptyMap<K,V>()
     * Key - Имя поля внутри json object
     * Val - С конвертированное значения поля внутри json object
     * */
    infix fun <V> map(valueMapper: (JsonNode) -> V): JsonNodeMapper<Map<String, V>> =
        JsonNodeMapper { node ->
            node.mapIfNotNull {
                fields().asSequence().associate { (key, value) -> key to valueMapper.invoke(value) }
            } ?: emptyMap()
        }

    /** Если Узел не найден или коллекция пуста -> return emptyMap<K,V>()
     * Key - Имя поля внутри json object
     * Val - С конвертированное в String значения поля внутри json object
     * ВАЖНО! null и другие примитивы не считая String, будут выглядит так: "null", "123", "true" и т.д.
     * */
    val mapStrings: JsonNodeMapper<Map<String, String>> get() = mapStringMapper

    /*
    **********************************************************
    * Mutable Collections
    **********************************************************
     */


    /** Если Узел не найден или коллекция пуста -> return mutableListOf<E>() */
    infix fun <E> mutableList(elemMapper: (JsonNode) -> E): JsonNodeMapper<MutableList<E>> =
        JsonNodeMapper { node ->
            node.mapIfNotNull {
                elements().asSequence().map(elemMapper).toMutableList()
            } ?: mutableListOf()
        }

    /** Если Узел не найден или коллекция пуста -> return mutableSetOf<E>() */
    infix fun <E> mutableSet(elemMapper: (JsonNode) -> E): JsonNodeMapper<Set<E>> =
        JsonNodeMapper { node ->
            node.mapIfNotNull {
                elements()!!.asSequence().map(elemMapper).toMutableSet()
            } ?: mutableSetOf()
        }

    /** Если Узел не найден или коллекция пуста -> return mutableMapOf<K,V>() */
    infix fun <K, V> mutableMap(elemMapper: (String, JsonNode) -> Pair<K, V>): JsonNodeMapper<MutableMap<K, V>> =
        JsonNodeMapper { node ->
            node.mapIfNotNull {
                fields().asSequence().map { (key, value) -> elemMapper.invoke(key, value) }.toMap(LinkedHashMap())
            } ?: mutableMapOf()
        }

    /** Если Узел не найден или коллекция пуста -> return emptyMap<K,V>()
     * Key - Имя поля внутри json object
     * Val - С конвертированное значения поля внутри json object
     * */
    infix fun <V> mutableMap(valueMapper: (JsonNode) -> V): JsonNodeMapper<MutableMap<String, V>> =
        JsonNodeMapper { node ->
            node.mapIfNotNull {
                fields().asSequence().map { (key, value) -> key to valueMapper.invoke(value) }
                    .toMap(LinkedHashMap())
            } ?: mutableMapOf()
        }

    /** Если Узел не найден или коллекция пуста -> return emptyMap<K,V>()
     * Key - Имя поля внутри json object
     * Val - С конвертированное в String значения поля внутри json object
     * ВАЖНО! null и другие примитивы не считая String, будут выглядит так: "null", "123", "true" и т.д.
     * */
    val mutableMapStrings: JsonNodeMapper<MutableMap<String, String>> get() = mutableMapStringMapper

    /*
    **********************************************************
    * Object and etc...
    **********************************************************
     */


    infix fun <E> obj(objectMapper: (JsonNode) -> E): JsonNodeMapper<E> =
        JsonNodeMapper { node -> node.mapIfNotNull(objectMapper) ?: err() }

    infix fun <E> objNull(objectMapper: (JsonNode) -> E): JsonNodeMapper<E?> =
        JsonNodeMapper { node -> node.mapIfNotNull(objectMapper) }


    companion object {
        private val dateTimeMapper = DateTimeFormatter.ISO_DATE_TIME
        private val dateMapper = DateTimeFormatter.ISO_DATE
        fun err(): Nothing = throw RuntimeException("Required not null value")
        val intMapper = JsonNodeMapper { node -> node.mapIfNotNull { asInt() } ?: err() }
        val longMapper = JsonNodeMapper { node -> node.mapIfNotNull { asLong() } ?: err() }
        val doubleMapper = JsonNodeMapper { node -> node.mapIfNotNull { asDouble() } ?: err() }
        val booleanMapper = JsonNodeMapper { node -> node.mapIfNotNull { asBoolean() } ?: err() }
        val textMapper = JsonNodeMapper { node -> node.mapIfNotNull { asText() } ?: err() }
        val textNullMapper = JsonNodeMapper { node -> node.mapIfNotNull { asText() } }
        val textEmptyMapper = JsonNodeMapper { node -> node.mapIfNotNull { asText() } ?: "" }

        val uuidMapper = JsonNodeMapper { node -> node.mapIfNotNull { asText().let(UUID::fromString) } ?: err() }
        val uuidNullMapper = JsonNodeMapper { node -> node.mapIfNotNull { asText().let(UUID::fromString) } }
        val bigIntegerMapper = JsonNodeMapper { node -> node.mapIfNotNull { bigIntegerValue() } ?: err() }
        val bigIntegerNullMapper = JsonNodeMapper { node -> node.mapIfNotNull { bigIntegerValue() } }
        val bigDecimalMapper = JsonNodeMapper { node -> node.mapIfNotNull { asText().let(::BigDecimal) } ?: err() }
        val bigDecimalNullMapper = JsonNodeMapper { node -> node.mapIfNotNull { asText().let(::BigDecimal) } }
        val localDateMapper =
            JsonNodeMapper { node -> node.mapIfNotNull { asText().let { LocalDate.parse(it, dateMapper) } } ?: err() }
        val localDateNullMapper =
            JsonNodeMapper { node -> node.mapIfNotNull { asText().let { LocalDate.parse(it, dateMapper) } } }
        val offsetDateTimeMapper =
            JsonNodeMapper { node ->
                node.mapIfNotNull { asText().let { OffsetDateTime.parse(it, dateTimeMapper) } } ?: err()
            }
        val offsetDateTimeNullMapper =
            JsonNodeMapper { node -> node.mapIfNotNull { asText().let { OffsetDateTime.parse(it, dateTimeMapper) } } }

        val mapStringMapper = JsonNodeMapper { node ->
            node.mapIfNotNull {
                this.fields().asSequence().associate { (key, value) -> key to value.asText() }
            } ?: emptyMap()
        }
        val mutableMapStringMapper = JsonNodeMapper { node ->
            node.mapIfNotNull {
                this.fields()!!.asSequence().map { (key, value) -> key to value.asText() }.toMap(LinkedHashMap())
            } ?: mutableMapOf()
        }

        /**
         * [JsonNode.get] эта штука может вернуть под видом null:
         * - null
         * - [com.fasterxml.jackson.databind.node.NullNode]
         * - [com.fasterxml.jackson.databind.node.MissingNode]
         * Поэтому обычной проверки на null в виде `node?.let{}` может быть недостаточно в Некоторых ситуациях.
         */
        inline fun <E> JsonNode?.mapIfNotNull(mapper: JsonNode.() -> E): E? =
            if (this == null ||
                this.nodeType == JsonNodeType.NULL ||
                this.nodeType == JsonNodeType.MISSING
            ) null else this.mapper()

    }

}

/**
 * Тут нужен reified, а без inline его не сделать,
 * а inline у interface методов могут быть только private OR final fun
 * IDEA: 'inline' modifier is not allowed on virtual members. Only private or final members can be inlined.
 *
 * так-то это часть interface [JsonAdapter].
 */
inline fun <reified E : Enum<E>> JsonAdapter.enumString(): JsonNodeMapper<E> {
    return JsonNodeMapper { node -> node.mapIfNotNull { enumValueOf<E>(this.asText()) } ?: err() }
}

inline fun <reified E : Enum<E>> JsonAdapter.enumStringNull(): JsonNodeMapper<E?> {
    return JsonNodeMapper { node -> node.mapIfNotNull { enumValueOf<E>(this.asText()) } }
}


class JsonDelegate<T>(
    private val invokedJsonNode: JsonNode,
    var fieldName: String,
    private val fieldExtractor: JsonAdapterFieldExtractor,
    private val valueMapper: JsonNodeMapper<T>
) : ReadOnlyProperty<JsonAdapter, T> {
    private var _value: Any? = UNINITIALIZED_VALUE

    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: JsonAdapter, property: KProperty<*>): T {
        if (_value !== UNINITIALIZED_VALUE) return _value as T
        if (fieldName == PROPERTY_NAME) fieldName = property.name

        try {
            val field = fieldExtractor.invoke(invokedJsonNode, fieldName)
            _value = valueMapper.invoke(field)
            return _value as T
        } catch (innerException: Exception) {
            throw JsonLazyParseException(
                "Json lazy parse Failed for Class \"${thisRef::class.qualifiedName}\"! \r\n" +
                        "propertyName: \"${property.name}\", propertyType: \"${property.returnType}\", jsonName: \"${fieldName}\"",
                innerException
            )
        }
    }

    private object UNINITIALIZED_VALUE
}

infix fun <T> JsonDelegate<T>.from(newFieldName: String): JsonDelegate<T> =
    this.apply { fieldName = newFieldName }

infix fun <T> JsonNode.parse(mapper: JsonNodeMapper<T>): JsonDelegate<T> =
    JsonDelegate(this, PROPERTY_NAME, defaultExtractor, mapper)

const val PROPERTY_NAME = "PROPERTY_NAME"