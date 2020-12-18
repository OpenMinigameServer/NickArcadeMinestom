package io.github.nickacpt.nickarcade.utils

import cloud.commandframework.arguments.standard.EnumArgument
import org.apache.commons.lang.reflect.FieldUtils
import java.util.*

var <C, T : Enum<T>?>(EnumArgument.EnumParser<C, T>).allowedValues: EnumSet<T>?
    get() {
        return FieldUtils.readDeclaredField(this, "allowedValues", true) as? EnumSet<T>
    }
    set(value) {
        FieldUtils.writeDeclaredField(this, "allowedValues", value, true)
    }