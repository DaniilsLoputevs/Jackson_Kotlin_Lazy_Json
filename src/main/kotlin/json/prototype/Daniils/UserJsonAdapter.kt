package json.prototype.Daniils//package json.prototype
//
//import com.fasterxml.jackson.databind.JsonNode
//
//
///**
// * - Поля могут быть перемещены
// * - Поля могут быть удаленны
// * - Поля могут быть добавлены
// * - Поля могут быть nullable
// * - Поля могут быть non-nullable
// * - Поля могут иметь различные с json имена
// * - поле переименовано(разные версии имеют разные [имена, типы, расположения] )
// *
// * - примитивы буду с дефолтным именем
// * - проверить что транслятор принимает JsonNode? если в json его нет
// * - exception если нода не найдена
// * -
// */
//class UserJsonAdapter(override val json: JsonNode) : JsonAdapter {
//    val id: String by json asText ("super_id" to { this.toString() })
//    val id: String by json asText "super_id" then { it }
//    val id: String by json asText { this.toString() }
//
//    //    val id: String by json field "path_value" asText
//    val id: String by json field "path_value" asType { it }
//    val id: String by json asText ::string
//    val id: String by json asText asText "path_value"
//    val id: String by json asType { "" } field "path_value"
//    val id: String by json.get("path_value") then { "" }
//    val id: String by asText()
//
//    //    val id: String by json[0] withName "" asType
//    val id: String by json["in_field"] <= asText()
//
//
//    val id by json[0] parse ::toPledges from "" // вот это
//    val id: List<String> by json[0] asType ::toPledges from "" // вот это
//    val id by json[0] asText "id"  // вот это
//
//    val id by json[0].text  // вот это
//    val id by json[0].asText  // вот это
//    val id by json[0].parseText  // вот это
//
//    val id by json[0] parse text from ""
//    val id by json[0] parse textNull from ""
//    val id by json[0] parse ::toText
//
//
//    //    infix fun <T> T.asText(none : Any? = null): T = func(this)
////    infix fun <T,R> T.asType(func: (T) -> R): R = func(this)
//    infix fun <T> T.then(func: (T) -> T): T = func(this)
//    fun <T> Lazy<T>.map(mapper: (JsonNode) -> T): Lazy<T> = TODO()
//
//    //    val id: String by json.uuid { asText() }
//    val email: String by json("ext_email") { asText() }
//    val serialVersion: Int by json("v3") { asInt() }
//
//    //    val metaInfo : JsonAdapterUserMetaInfo by json { JsonAdapterUserMetaInfo(this) }
//    val metaInfo: JsonAdapterUserMetaInfo by json(::JsonAdapterUserMetaInfo)
//
//    val contacts: Set<JsonAdapterContact> by jsonSet("ext_contacts") { JsonAdapterContact(this) }
////    val contacts  by jsonSet("ext_contacts", ::JsonAdapterContact)
//
//
//    private fun toPledges(node: JsonNode?): List<String> = TODO()
//}
//
//
//class JsonAdapterUserMetaInfo(private val jsonNode: JsonNode)
//
//class JsonAdapterContact(private val jsonNode: JsonNode)
//
//fun main() {
//    val node: JsonNode
//
//    val k = UserJsonAdapter::class.members
//    k.forEachIndexed { index, kCallable ->
//        println("$index :: $kCallable || ${kCallable::class.simpleName}")
//        when (kCallable.name) {
////            "id" -> (kCallable as KMutableProperty1<*, *>).apply { println("ID=${this.set}") }
//            "email" -> println("EMAIL")
//        }
//    }
//
//    val obj: Any = ""
//    when (obj) {
//        ::isText -> println("Text")
//        ::isNumeric -> println("Numeric")
//        ::isArray -> println("Array")
//    }
//}
//
//fun isText(obj: Any): Boolean = obj is String
//fun isNumeric(obj: Any): Boolean = obj is Number
//fun isArray(obj: Any): Boolean = obj.javaClass.isArray
//
////class JsonDelegate(private var resource: Resource = Resource()) {
////    operator fun getValue(thisRef: Owner, property: KProperty<*>): Resource {
////        return resource
////    }
////    operator fun setValue(thisRef: Owner, property: KProperty<*>, value: Any?) {
////        if (value is Resource) {
////            resource = value
////        }
////    }
////}