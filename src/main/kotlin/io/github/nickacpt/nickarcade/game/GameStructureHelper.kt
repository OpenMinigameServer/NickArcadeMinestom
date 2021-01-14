package io.github.nickacpt.nickarcade.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.session.ClipboardHolder
import io.github.nickacpt.nickarcade.game.definition.position.GamePosition
import io.github.nickacpt.nickarcade.schematics.manager.SchematicManager
import io.github.nickacpt.nickarcade.schematics.manager.SchematicName
import io.github.nickacpt.nickarcade.schematics.manager.clipboard
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.Instance

object GameStructureHelper {

    fun createWaitingLobby(instance: Instance): GamePosition? {
        val clipboard = SchematicManager.getClipboard(SchematicName.LOBBY) ?: return null
        var y = Chunk.CHUNK_SIZE_Y - clipboard.dimensions.y.toFloat()
        val instanceClipboard = instance.clipboard
        if (instanceClipboard != null) {
            y =
                (instanceClipboard.maximumPoint.y + (instanceClipboard.origin.subtract(instanceClipboard.minimumPoint)).y).toFloat()
        }
        val finalPos = GamePosition(0f, y, 0f)

        val asWorld = MinestomAdapter.asWorld(instance)
        WorldEdit.getInstance().newEditSessionBuilder().world(asWorld).build().use { editSession ->
            val operation: Operation = ClipboardHolder(clipboard)
                .createPaste(editSession)
                .to(MinestomAdapter.asBlockVector(finalPos.toMinestom()))
                .build()
            Operations.complete(operation)
        }
        return finalPos
    }

}