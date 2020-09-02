package com.pullo.batch.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.io.IOException

class CstDateTimeDeserializer : JsonDeserializer<DateTime>() {
    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): DateTime? {
        val currentToken = jsonParser.currentToken
        if (currentToken == JsonToken.VALUE_STRING) {
            return DateTime.parse(jsonParser.text.trim()).withZone(DateTimeZone.forID("Asia/Shanghai"))
        }
        return null
    }

    override fun handledType(): Class<DateTime> {
        return DateTime::class.java
    }
}
