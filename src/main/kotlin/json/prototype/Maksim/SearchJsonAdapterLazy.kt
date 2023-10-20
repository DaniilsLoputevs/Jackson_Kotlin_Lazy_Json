package org.json

//import com.fasterxml.jackson.annotation.JsonInclude
//import com.fasterxml.jackson.databind.JsonNode
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import java.time.OffsetDateTime
//
//fun main() {
//    val objectMapper = jacksonObjectMapper().apply {
//        this.registerModule(JavaTimeModule())
//        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
//    }
//    val node = objectMapper.createObjectNode().also {
//        it.put("Id", "456132dsfg4df6g1d32fg")
//        it.put("CreditApplicationId", "1IPO20456")
//        it.put("Version", 8)
//        it.put("ExternalId", "data")
//        it.put("CreateDate", OffsetDateTime.now().toString())
//    }
//    val json = SearchJsonAdapterLazy(node)
//    println(json)
//}
//
//class Migrator()
//
//class SearchJsonAdapterLazy(override val ext: JsonNode): IJsonAdapter {
//
//    val id: String? by lazyJson("id") { it.asText() }
//    val creditApplicationId: String? by lazyJson("creditApplicationId") { it.asText() }
//    val version: Int? by lazyJson("version") { it.asInt() }
//    val externalId: String? by lazyJson("externalId") { it.asText() }
//    val clientHandlingId: String? by lazyJson("clientHandlingId") { it.asText() }
//    val createDate: OffsetDateTime? = null
//    val updateDate: OffsetDateTime? = null
//    val type: String? by "Type" asTextOrNullFrom ext
//
//    val pledges: List<String> by ext asTypeFrom ::toPledge
//
//    val type2: String? by asText()
//    val pledges2: List<String> by asType("ExtPledgeSearch", ::toPledge)
//
//    var insuranceList: List<Any>? = null
//
//    override fun toString(): String {
//        return """SearchJsonAdapter(
//            id=$id, creditApplicationId=$creditApplicationId, version=$version,
//            externalId=$externalId, clientHandlingId=$clientHandlingId, createDate=$createDate,
//            updateDate=$updateDate, type=$type)""".trimMargin()
//    }
//}