package io.github.nickacpt.nickarcade.mixins;

import io.github.openminigameserver.worldedit.platform.adapters.MinestomPermissionsProvider;
import net.minestom.server.entity.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinestomPermissionsProvider.class)
public abstract class MinestomPermissionsProviderMixin {

    @Inject(method = "hasPermission", at = @At("HEAD"), cancellable = true)
    public void onWorldEditCheck(Player player, String permission, CallbackInfoReturnable<Boolean> cir) {
        if (permission.startsWith("worldedit.") && player.hasPermission("worldedit.*", null))
            cir.setReturnValue(true);
    }

}
