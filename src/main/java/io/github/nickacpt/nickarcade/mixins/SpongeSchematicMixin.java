package io.github.nickacpt.nickarcade.mixins;

import dev.sejtam.mineschem.core.schematic.SpongeSchematic;
import io.github.nickacpt.nickarcade.SchematicSizeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpongeSchematic.class)
public class SpongeSchematicMixin implements SchematicSizeHelper {
    @Shadow
    private Short width;

    @Shadow
    private Short height;

    @Shadow
    private Short length;

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getLength() {
        return length;
    }
}
