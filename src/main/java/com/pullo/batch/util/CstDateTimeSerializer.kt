package com.pullo.batch.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.joda.time.DateTime
import java.io.IOException

class CstDateTimeSerializer : JsonSerializer<DateTime>() {
    @Throws(IOException::class)
    override fun serialize(
        dateTime: DateTime, jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) {
        jsonGenerator.writeString(dateTime.toString("yyyy-MM-dd'T'HH:mm:ssZZ"))
    }

    override fun handledType(): Class<DateTime> {
        return DateTime::class.java
    }
}
