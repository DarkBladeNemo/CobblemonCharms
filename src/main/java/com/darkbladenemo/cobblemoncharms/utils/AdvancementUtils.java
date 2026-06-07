package com.darkbladenemo.cobblemoncharms.utils;

import com.darkbladenemo.cobblemoncharms.common.event.AdvancementSyncEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementUtils {

    /**
     * Grants all criteria for an advancement if it hasn't been completed yet.
     * Also triggers a client sync so tooltip state updates immediately.
     *
     * @return {@code true} if the advancement was newly granted.
     */
    public static boolean grantAdvancement(ServerPlayer player, AdvancementHolder advancement) {
        if (advancement == null) return false;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) return false;

        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }

        AdvancementSyncEvents.sendSnapshot(player);

        return true;
    }
}