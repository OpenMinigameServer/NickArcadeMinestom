package io.github.nickacpt.nickarcade.mixins;

import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.listener.BlockPlacementListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(BlockPlacementListener.class)
public class BlockPlacementListenerMixin {

    @Redirect(method = "listener", at = @At(value = "INVOKE", target = "Lnet/minestom/server/instance/Instance;" +
            "getChunkEntities(Lnet/minestom/server/instance/Chunk;)Ljava/util/Set;"))
    private static Set<Entity> onGetChunkEntities(Instance instance, Chunk chunk) {
        return instance.getEntities();
    }
}
