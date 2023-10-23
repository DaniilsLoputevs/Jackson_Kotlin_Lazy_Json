package json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.MissingNode

/**
 * Поддержка типов:
 * Int
 * Double
 * Boolean
 * String
 * UUID
 * BigDecimal
 * LocalDate
 * OffsetDateTime
 *
 * List<Any> - логика вложенных сущностей
 * Set<Any> - логика вложенных сущностей
 * Map<Any, Any> - логика вложенных сущностей
 *
 * MutableList<Any> - логика вложенных сущностей
 * MutableSet<Any> - логика вложенных сущностей
 * MutableMap<Any, Any> - логика вложенных сущностей
 *
 * Enum - (передаём enum туда, для поиска из имеющихся значений)
 * Any - логика вложенных сущностей
 */
class UserJsonAdapter(override val json: JsonNode) : JsonAdapter {
    val version by json parse int from "v_id"
    val intField by json parse int
    val doubleField by json parse double
    val booleanField by json parse boolean
    val textField by json parse text
    val textNullField by json parse textNull

    val uuidField by json parse uuid
    val uuidNullField by json parse uuidNull
    val bigIntegerField by json parse bigInteger
    val bigIntegerNullField by json parse bigIntegerNull
    val bigDecimalField by json parse bigDecimal
    val bigDecimalNullField by json parse bigDecimalNull
    val offsetDateTimeField by json parse offsetDateTime
    val offsetDateTimeNullField by json parse offsetDateTimeNull
    val localDateField by json parse localDate
    val localDateNullField by json parse localDateNull

    val listField by json parse list(text)
    val setField by json parse set(text)
    val mapField by json parse map(int)
    val mapStringsField by json parse mapStrings

    // Можно передавать ссылку на функцию, что принимает Map.Entry<String, JsonNode?> - поддержка разного API
    val mapEntriesField by json parse map { fieldName, node -> fieldName to (node?.asText() ?: "null") }

    val mutableListField by json parse mutableList(text)
    val mutableSetField by json parse mutableSet(text)

    // Можно передавать ссылку на функцию ИЛИ Лямбду, что имеет 2 параметра: String, JsonNode? - поддержка разного API
    val mutableMapField by json parse mutableMap { fieldName, node ->
        "key_prefix__$fieldName" to node?.asText()?.all { char -> char.isDigit() }
    }

    val balances by json parse list(obj(::BalanceJsonAdapter)) from "balance_list"
    val pledges by json parse set(obj(::JsonAdapterUserPledges)) from "pledge_set"


    val innerField by json parse obj(::InnerJsonAdapter)
    val innerNullField by json parse objNull(::InnerJsonAdapter)
    val innerComputeField by json parse objOrCompute(::InnerJsonAdapter) { InnerJsonAdapter(MissingNode.getInstance()) }

    val notExistInJsonButRequiredField by json parse int from "not_exist_json"
}

enum class PledgesStatus {
    START, ACTIVE, CLOSE;
}

class BalanceJsonAdapter(override val json: JsonNode) : JsonAdapter {
    val currency by json parse textNull
    val amount by json parse bigDecimal
}

class JsonAdapterUserPledges(override val json: JsonNode) : JsonAdapter {
    val id by json parse long
    val isActive by json parse boolean
    val status by json parse enumString<PledgesStatus>()
    val statusNull by json parse enumStringNull<PledgesStatus>()
}

class InnerJsonAdapter(override val json: JsonNode) : JsonAdapter {
    val id by json parse bigInteger from "NestedId"
    val alisa by json parse textEmpty from "NestedAlias"
}