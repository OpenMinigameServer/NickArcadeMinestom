package io.github.nickacpt.nickarcade.mixins;

import io.github.nickacpt.nickarcade.AlternateIdContainer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements AlternateIdContainer {
    UUID alternateId;

    public PlayerMixin(@NotNull EntityType entityType) {
        super(entityType);
    }

    @Override
    public @NotNull UUID getAlternateUuid() {
        if (alternateId == null)
            alternateId = getUuid();
        return alternateId;
    }

    @Override
    public void setAlternateUuid(@NotNull UUID entityUuid) {
        alternateId = entityUuid;
    }
}
