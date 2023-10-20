package json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.node.MissingNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * UUID
 * String
 * Int
 * BigDecimal
 * OffsetDateTime
 * LocalDate
 * Boolean
 * Enum - (передаём enum туда, для поиска из имеющихся значений)
 *
 * List<Any> - логика вложенных сущностей
 * Set<Any> - логика вложенных сущностей
 * Map<Any, Any> - логика вложенных сущностей
 *
 * MutableList<Any> - логика вложенных сущностей
 * MutableSet<Any> - логика вложенных сущностей
 * MutableMap<Any, Any> - логика вложенных сущностей
 *
 *
 * Any - логика вложенных сущностей
 * TODO - parse collection from null element [1,2, null, 4, bull, 5]
 * TODO - ? make as Abstract class
 */
class JsonAdapterTest : FunSpec({
    val testUuid = UUID.fromString("51381d46-056d-4091-ae43-0fcf553c7387")
    val testDate = LocalDate.of(2023, 10, 19)
    val testOffsetDateTime = OffsetDateTime.of(
        testDate,
        LocalTime.of(15, 15),
        ZoneOffset.UTC
    )
    val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    val dateFormatter = DateTimeFormatter.ISO_DATE
    val rubAmount = "10000000"

    val objectMapper = jacksonObjectMapper().apply {
        this.registerModule(JavaTimeModule())
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
    val root = objectMapper.createObjectNode().apply {
        put("v_id", "1")
        put("intField", 2)
        put("doubleField", 8.5)
        put("booleanField", true)
        put("textField", "text_value")
        putNull("textNullField")

        put("uuidField", testUuid.toString())
        putNull("uuidNullField")
        put("bigIntegerField", BigInteger.valueOf(111L))
        putNull("bigIntegerNullField")
        put("bigDecimalField", BigDecimal(12.75))
        putNull("bigDecimalNullField")
        put("offsetDateTimeField", testOffsetDateTime.format(dateTimeFormatter))
        putNull("offsetDateTimeNullField")
        put("localDateField", testDate.format(dateFormatter))
        putNull("localDateNullField")

        putArray("listField").add("aaa").add("bbb").add("ccc")
        putArray("setField").add("aaa").add("bbb").add("ccc")
        putObject("mapField").put("a", "1").put("b", "2").put("c", "3")
        putObject("mapStringsField").putNull("a").put("b", 2).put("c", "3")

        putArray("mutableListField").add("aaa").add("bbb").add("ccc")
        putArray("mutableSetField").add("aaa").add("bbb").add("ccc")
        putObject("mutableMapField").put("a", "1").put("b", "2").put("c", "3")

        putArray("balance_list").apply {
            addObject().put("currency", "USD").put("amount", 10)
            addObject().put("currency", "RUB").put("amount", rubAmount)
            addObject().put("currency", "EUR").put("amount", BigDecimal(25.75))
        }
        putArray("pledge_set").addObject()
            .put("id", 312L)
            .put("isActive", false)
            .put("status", "CLOSE")
            .putNull("statusNull")

        putObject("innerField")
            .put("NestedId", Long.MAX_VALUE)
            .putNull("NestedAlias")

        putNull("innerNullField")

    }


    test("complex happy pass! @_@") {
        UserJsonAdapter(root).apply {
            shouldThrow<JsonLazyParseException> {
                this.notExistInJsonButRequiredField // getter invoke
            }

            version shouldBe 1
            intField shouldBe 2
            doubleField shouldBe 8.5
            booleanField shouldBe true
            textField shouldBe "text_value"
            textNullField shouldBe null
            uuidField shouldBe testUuid
            uuidNullField shouldBe null
            bigIntegerField shouldBe BigInteger.valueOf(111L)
            bigIntegerNullField shouldBe null
            bigDecimalField shouldBe BigDecimal(12.75)
            bigDecimalNullField shouldBe null
            offsetDateTimeField shouldBe testOffsetDateTime
            offsetDateTimeNullField shouldBe null
            localDateField shouldBe testDate
            localDateNullField shouldBe null

            listField shouldBe listOf("aaa", "bbb", "ccc")
            setField shouldBe setOf("aaa", "bbb", "ccc")
            mapField shouldBe mapOf("a" to 1, "b" to 2, "c" to 3)
            mapStringsField shouldBe mapOf("a" to "null", "b" to "2", "c" to "3")

            mutableListField shouldBe mutableListOf("aaa", "bbb", "ccc")
            mutableSetField shouldBe mutableSetOf("aaa", "bbb", "ccc")
            mutableMapField shouldBe mutableMapOf(
                "key_prefix__a" to true,
                "key_prefix__b" to true,
                "key_prefix__c" to true
            )

            balances shouldHaveSize 3
            balances[0].apply {
                currency shouldBe "USD"
                amount shouldBe BigDecimal.TEN
            }
            balances[1].apply {
                currency shouldBe "RUB"
                amount shouldBe BigDecimal(rubAmount)
            }
            balances[2].apply {
                currency shouldBe "EUR"
                amount shouldBe BigDecimal(25.75)
            }

            pledges shouldHaveSize 1
            pledges.first().apply {
                id shouldBe 312L
                isActive shouldBe false
                status shouldBe PledgesStatus.CLOSE
                statusNull shouldBe null
            }

            innerField.apply {
                id shouldBe BigInteger.valueOf(Long.MAX_VALUE)
                alisa shouldBe ""
            }

            innerNullField shouldBe null
            innerComputeField shouldBe MissingNode.getInstance()
        }

    }

})