package io.github.nickacpt.nickarcade.schematics;

import io.github.nickacpt.nickarcade.schematics.utils.Region;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

public interface ISchematic {

    ErrorMessage read();

    ErrorMessage write(@NotNull Region region);

    ErrorMessage build(@NotNull Position position);

    enum ErrorMessage {
        NoSuchFile,
        NotLoaded,
        NBTName,
        NBTWidth,
        NBTHeight,
        NBTLength,
        NBTMaxPalette,
        NBTPalette,
        PaletteNotEqualsMaxPalette,
        PaletteGetInt,
        NBTBlockData,
        BadRead,
        BadWrite,
        VarIntSize,
        NoBlocks,
        BadMaterials,
        Instance,
        BlockBatch,
        None
    }

}
