package org.json

//import com.fasterxml.jackson.databind.JsonNode
//
//interface IJsonAdapter {
//
//    val ext: JsonNode
//
//    infix fun String.asTextOrNullFrom(node: JsonNode?): Lazy<String?> = lazy { null }
//
//    infix fun String.asTextFrom(node: JsonNode?): Lazy<String> = lazy { "" }
//
//    infix fun <T> JsonNode?.asTypeFrom(code: (JsonNode?) -> Lazy<T>): Lazy<T> = code.invoke(this)
//
//    fun asText(name: String = "", node: JsonNode? = ext): Lazy<String?> =
//         lazy { node?.get(name)?.asText() }
//
//    fun <T> asType(name: String = "", code: (JsonNode?) -> Lazy<T>): Lazy<T> = code.invoke(ext)
//
//
//    fun toPledge(node: JsonNode?): Lazy<List<String>> =
//        lazy {
//            node?.let {
//                if (it.has("ExtPledgeSearch")) {
//                    it.get("ExtPledgeSearch").get("Cadastrals").map { it.asText() }
//                } else null
//            }
//                ?: emptyList<String>()
//        }
//
//    fun <T> lazyJson(
//        name: String,
//        data: (() -> JsonNode?) = {
//            ext.get(name.capitalize())
//        },
//        converter: (JsonNode) -> T?
//    ) =
//        lazy(LazyThreadSafetyMode.NONE) {
//            val foundJson = data.invoke()
//            foundJson?.let {
//                converter.invoke(it)
//            }
//        }
//}