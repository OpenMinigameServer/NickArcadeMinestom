package io.github.nickacpt.nickarcade.chat

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration.BOLD

enum class ChatEmote(val replacement: ComponentLike, emote: String? = null) {
    HEART(text("❤", RED), "<3"),
    STAR(text("✮", GOLD)),
    YES(text("✔", GREEN)),
    NO(text("✖", RED)),
    JAVA(text("☕", AQUA)),
    ARROW(text("➜", YELLOW)),
    SHRUG(text("¯\\_(ツ)_/¯", YELLOW)),
    TABLEFLIP(text {
        it.append(text("(╯°□°）╯", RED))
        it.append(text("︵", WHITE))
        it.append(text(" ┻━┻", GRAY))
    }),
    HEY(text("( ﾟ◡ﾟ)/", LIGHT_PURPLE), "o/"),
    ONE_TWO_THREE(text {
        it.append(text("1", GREEN))
        it.append(text("2", YELLOW))
        it.append(text("3", RED))
    }, ":123:"),
    TOTEM(text {
        it.append(text("☉", AQUA))
        it.append(text("_", YELLOW))
        it.append(text("☉", AQUA))
    }),
    TYPING(text {
        it.append(text("✎", YELLOW))
        it.append(text("...", GOLD))
    }),
    MATHS(text {
        it.append(text("√", GREEN))
        it.append(text("(", YELLOW, BOLD))
        it.append(text("π", GREEN))
        it.append(text("+x", GREEN, BOLD))
        it.append(text(")", YELLOW, BOLD))
        it.append(text("=", GREEN, BOLD))
        it.append(text("L", RED, BOLD))
    }),
    SNAIL(text {
        it.append(text("@", YELLOW))
        it.append(text("'", GREEN))
        it.append(text("-", YELLOW))
        it.append(text("'", GREEN))
    }),
    THINKING(text {
        it.append(text("(", GOLD))
        it.append(text("0", GREEN))
        it.append(text(".", GOLD))
        it.append(text("o", GREEN))
        it.append(text("?", RED))
        it.append(text(")", GOLD))
    }),
    GIMME(text("༼つ◕_◕༽つ", AQUA)),
    WIZARD(text {
        it.append(text("(", YELLOW))
        it.append(text("'", AQUA))
        it.append(text("-", YELLOW))
        it.append(text("'", AQUA))
        it.append(text(")⊃", YELLOW))
        it.append(text("━", RED))
        it.append(text("☆ﾟ.*･｡ﾟ", LIGHT_PURPLE))
    }),
    PVP(text("⚔", YELLOW)),
    PEACE(text("✌", GREEN)),
    OOF(text("OOF", RED, BOLD)),
    PUFFER(text("<('O')>", YELLOW, BOLD)),
    DAB(text {
        it.append(text("<", LIGHT_PURPLE))
        it.append(text("o", YELLOW))
        it.append(text("/", LIGHT_PURPLE))
    }),
    DOG(text("(ᵔᴥᵔ)", GOLD)),
    CAT(text {
        it.append(text("= ", YELLOW))
        it.append(text("＾● ⋏ ●＾", AQUA))
        it.append(text(" =", YELLOW))
    }),
    SLOTH(text {
        it.append(text("(", GOLD))
        it.append(text("・", DARK_GRAY))
        it.append(text("⊝", GOLD))
        it.append(text("・", DARK_GRAY))
        it.append(text(")", GOLD))
    }),
    HAPPY(text("^-^", GREEN), "^-^"),
    CUTE(text {
        it.append(text("(", YELLOW))
        it.append(text("✿", GREEN))
        it.append(text("◠‿◠)", YELLOW))
    }),
    SNOW(text("☃", AQUA)),
    YEY(text("ヽ (◕◡◕) ﾉ", GREEN)),
    HAPPY_2(text("^_^", GREEN), "^_^"),
    HEY_2(text("ヽ(^◇^*)/", YELLOW), "h/"),
    DJ(text {
        it.append(text("ヽ", BLUE))
        it.append(text("(", DARK_PURPLE))
        it.append(text("⌐", LIGHT_PURPLE))
        it.append(text("■", RED))
        it.append(text("_", GOLD))
        it.append(text("■", YELLOW))
        it.append(text(")", AQUA))
        it.append(text("ノ", DARK_AQUA))
        it.append(text("♬", BLUE))
    }),
    ;

    val emote: String = emote ?: ":${name.toLowerCase()}:"

}