package io.github.nickacpt.nickarcade.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

val separator = text(" ".repeat(64), Style.style(TextDecoration.STRIKETHROUGH))

fun separator(color: TextColor = NamedTextColor.BLUE): Component {
    return separator.style {
        it.decorate(TextDecoration.STRIKETHROUGH)
        it.color(color)
    }
}

fun separator(color: TextColor = NamedTextColor.BLUE, builder: (TextComponent.Builder).() -> Unit): Component {
    return text {
        it.append(separator(color))
        it.append(newline())
        it.append(text(builder))
        it.append(newline())
        it.append(separator(color))
    }
}