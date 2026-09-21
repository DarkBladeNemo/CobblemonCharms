package com.darkbladenemo.cobblemoncharms.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemGiveUtils {

    public static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    public static void giveOrDrop(ServerPlayer player, ItemStack stack, String messageKey, Object... args) {
        giveOrDrop(player, stack);
        player.sendSystemMessage(Component.translatable(messageKey, args));
    }
}