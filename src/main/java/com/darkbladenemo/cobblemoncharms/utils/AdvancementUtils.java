package com.darkbladenemo.cobblemoncharms.utils;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementUtils {

    /**
     * Grants all criteria for an advancement if it hasn't been completed yet.
     *
     * @param player      The player to award the advancement to.
     * @param advancement The advancement to award. No-op if null.
     * @return {@code true} if the advancement was newly granted, {@code false} if it was already completed.
     */
    public static boolean grantAdvancement(ServerPlayer player, AdvancementHolder advancement) {
        if (advancement == null) return false;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) return false;

        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }
        return true;
    }
}
