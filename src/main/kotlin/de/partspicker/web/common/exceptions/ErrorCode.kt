package de.partspicker.web.common.exceptions

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = ErrorCodeSerializer::class)
enum class ErrorCode(val code: Int) {
    EntityNotFound(4004);
}

class ErrorCodeSerializer : StdSerializer<ErrorCode>(ErrorCode::class.java) {
    override fun serialize(value: ErrorCode?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (value?.code != null) {
            gen!!.writeNumber(value.code)
        }
    }
}
