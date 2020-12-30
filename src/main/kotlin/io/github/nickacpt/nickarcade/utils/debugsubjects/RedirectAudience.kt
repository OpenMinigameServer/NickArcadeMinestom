package io.github.nickacpt.nickarcade.utils.debugsubjects

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.identity.Identified
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

class RedirectAudience(val audience: Audience, private val prefix: Component) : Audience {
    /**
     * Gets the audiences.
     *
     * @return the audiences
     * @since 4.0.0
     */
    override fun sendMessage(source: Identified, message: Component, type: MessageType) {
        audience.sendMessage(source, prefix.append(message), type)
    }

    override fun sendMessage(source: Identity, message: Component, type: MessageType) {
        audience.sendMessage(source, prefix.append(message), type)
    }

    override fun sendActionBar(message: Component) {
        audience.sendActionBar(prefix.append(message))
    }

    override fun sendPlayerListHeader(header: Component) {
        audience.sendPlayerListHeader(header)
    }

    override fun sendPlayerListFooter(footer: Component) {
    }

    override fun sendPlayerListHeaderAndFooter(header: Component, footer: Component) {
    }

    override fun showTitle(title: Title) {
    }

    override fun clearTitle() {
    }

    override fun resetTitle() {
    }

    override fun showBossBar(bar: BossBar) {
    }

    override fun hideBossBar(bar: BossBar) {
    }

    override fun playSound(sound: Sound) {
    }

    override fun playSound(sound: Sound, x: Double, y: Double, z: Double) {
    }

    override fun stopSound(stop: SoundStop) {
    }

    override fun openBook(book: Book) {
    }
}