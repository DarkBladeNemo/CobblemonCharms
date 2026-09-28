package com.darkbladenemo.cobblemoncharms.common.influence;

import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Poké Snack wrapper for Type Charms. Resolves the triggering player per weight
 * check and delegates to a cached per-player {@link TypeCharmInfluence}.
 * Non-player causes return the weight unchanged.
 */
public class PokeSnackTypeCharmInfluence implements SpawningInfluence {

    private final Map<UUID, TypeCharmInfluence> perPlayer = new ConcurrentHashMap<>();

    @Override
    public float affectWeight(@NotNull SpawnDetail detail,
                              @NotNull SpawnablePosition position,
                              float weight) {
        UUID id = position.getCause().getEntityUUID();
        if (id == null) return weight;

        TypeCharmInfluence delegate = perPlayer.get(id);
        if (delegate == null || delegate.isExpired()) {
            if (!(position.getCause().getEntity() instanceof ServerPlayer player)) return weight;
            perPlayer.values().removeIf(TypeCharmInfluence::isExpired);
            delegate = new TypeCharmInfluence(player);
            perPlayer.put(id, delegate);
        }
        return delegate.affectWeight(detail, position, weight);
    }

    @Override
    public void affectAction(@NotNull SpawnAction<?> action) { }

    @Override
    public void affectSpawn(@NotNull SpawnAction<?> action, @NotNull Entity entity) { }

    @Override
    public boolean isAllowedPosition(@NotNull ServerLevel world, @NotNull BlockPos pos,
                                     @NotNull SpawnablePositionCalculator<?, ?> calculator) {
        return true;
    }

    @Override
    public boolean affectSpawnable(@NotNull SpawnDetail detail, @NotNull SpawnablePosition position) {
        return true;
    }
}