package com.pullo.batch.util

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.joda.time.DateTime
import java.lang.reflect.Type
import java.util.HashMap

object JsonMapperFactory {
    /**
     * Get default json object mapper.
     */
    @JvmStatic
    val defaultMapper = ObjectMapper()

    /**
     * Get restrict json object mapper.
     */
    @JvmStatic
    val restrictMapper = ObjectMapper()

    init {
        defaultMapper.setVisibility(
            defaultMapper.visibilityChecker
                .withFieldVisibility(Visibility.ANY)
                .withGetterVisibility(Visibility.NONE)
                .withIsGetterVisibility(Visibility.NONE)
        )

        defaultMapper
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)
            // Fix jackson vs @ConstructorProperties issue: cannot deserialize json properly,
            // default values will be overwritten.
            .disable(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES)
            .apply {
                nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
            }

        // Serialize/deserialize Joda DateTime.
        defaultMapper
            .registerModule(object : JodaModule() {
                init {
                    addSerializer(DateTime::class.java, CstDateTimeSerializer())
                    addDeserializer(DateTime::class.java, CstDateTimeDeserializer())
                }
            })
            .registerModule(KotlinModule())
            .registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())

        restrictMapper.setVisibility(
            defaultMapper.visibilityChecker
                .withFieldVisibility(Visibility.NONE)
                .withGetterVisibility(Visibility.NONE)
                .withIsGetterVisibility(Visibility.NONE)
        )

        restrictMapper
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)
            // Fix jackson vs @ConstructorProperties issue: cannot deserialize json properly,
            // default values will be overwritten.
            .disable(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES)
            .apply {
                nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
            }

        // Serialize/deserialize Joda DateTime.
        restrictMapper
            .registerModule(object : JodaModule() {
                init {
                    addSerializer(DateTime::class.java, CstDateTimeSerializer())
                    addDeserializer(DateTime::class.java, CstDateTimeDeserializer())
                }
            })
            .registerModule(KotlinModule())
            .registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
    }

    @JvmStatic
    fun getListType(clazz: Class<*>): CollectionType {
        return defaultMapper.typeFactory.constructCollectionType(
            List::class.java, clazz
        )
    }

    @JvmStatic
    fun getMapType(keyClass: Class<*>, valueClass: Class<*>): MapType {
        return defaultMapper.typeFactory.constructMapType(
            HashMap::class.java,
            keyClass, valueClass
        )
    }

    @JvmStatic
    fun getType(type: Type): JavaType {
        return defaultMapper.typeFactory.constructType(type)
    }
}
