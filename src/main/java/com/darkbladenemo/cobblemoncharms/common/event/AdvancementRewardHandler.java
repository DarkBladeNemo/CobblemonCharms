package com.darkbladenemo.cobblemoncharms.common.event;

import com.darkbladenemo.cobblemoncharms.advancement.ModAdvancement;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Awards charm items when a player logs in and already has the corresponding advancement.
 * Handles cases where the advancement was granted offline or via /advancement.
 */
public class AdvancementRewardHandler {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            checkExpCharm(handler.player);
            checkCatchCharm(handler.player);
        });
    }

    public static void checkExpCharm(ServerPlayer player) {
        if (!Config.ENABLE_EXP_CHARM.get()) return;

        AdvancementHolder expCharmAdvancement =
                ModAdvancement.EXP_CHARM.getAdvancement(player.serverLevel());
        if (expCharmAdvancement == null) return;
        if (!player.getAdvancements().getOrStartProgress(expCharmAdvancement).isDone()) return;

        // Only give the item if they don't already have one
        boolean alreadyHas = player.getInventory().items.stream()
                .anyMatch(s -> s.is(ModItems.EXP_CHARM));
        if (alreadyHas) return;

        ItemStack stack = new ItemStack(ModItems.EXP_CHARM);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.sendSystemMessage(
                Component.translatable("message.cobblemoncharms.exp_charm_awarded"));
    }

    public static void checkCatchCharm(ServerPlayer player) {
        if (!Config.ENABLE_CATCH_CHARM.get()) return;

        AdvancementHolder catchCharmAdvancement =
                ModAdvancement.CATCH_CHARM.getAdvancement(player.serverLevel());
        if (catchCharmAdvancement == null) return;
        if (!player.getAdvancements().getOrStartProgress(catchCharmAdvancement).isDone()) return;

        // Only give the item if they don't already have one
        boolean alreadyHas = player.getInventory().items.stream()
                .anyMatch(s -> s.is(ModItems.CATCH_CHARM));
        if (alreadyHas) return;

        ItemStack stack = new ItemStack(ModItems.CATCH_CHARM);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.sendSystemMessage(
                Component.translatable("message.cobblemoncharms.catch_charm_awarded"));
    }
}