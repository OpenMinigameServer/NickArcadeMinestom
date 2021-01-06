package io.github.nickacpt.nickarcade;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface AlternateIdContainer {
    @NotNull UUID getAlternateUuid();

    void setAlternateUuid(@NotNull UUID entityUuid);
}
