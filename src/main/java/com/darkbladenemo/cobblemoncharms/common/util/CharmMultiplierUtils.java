package com.darkbladenemo.cobblemoncharms.common.util;

import com.darkbladenemo.cobblemoncharms.advancement.ModAdvancement;
import com.darkbladenemo.cobblemoncharms.common.component.ExpCharmData;
import com.darkbladenemo.cobblemoncharms.common.component.ShinyCharmData;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Function;

/**
 * Calculates charm multipliers from equipped Curio items, with additive stacking
 * across multiple charms of the same type.
 * <p>
 * When {@code CHARM_EFFECT_REQUIRES_ADVANCEMENT} is enabled, a charm only
 * contributes its multiplier if the player has earned the corresponding advancement.
 */
public class CharmMultiplierUtils {

    public static float getExpMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_EXP_CHARM.get()) return 1.0f;
        return getCharmMultiplier(
                player,
                "exp_charm_slot",
                ModItems.EXP_CHARM.get(),
                stack -> {
                    ExpCharmData data = stack.get(ModDataComponents.EXP_CHARM_DATA.get());
                    return data != null ? data.multiplier() : Config.EXP_CHARM_MULTIPLIER.get().floatValue();
                },
                ModAdvancement.EXP_CHARM
        );
    }

    public static float getShinyMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_SHINY_CHARM.get()) return 1.0f;
        return getCharmMultiplier(
                player,
                "shiny_charm_slot",
                ModItems.SHINY_CHARM.get(),
                stack -> {
                    ShinyCharmData data = stack.get(ModDataComponents.SHINY_CHARM_DATA.get());
                    return data != null ? data.multiplier() : Config.SHINY_CHARM_MULTIPLIER.get().floatValue();
                },
                ModAdvancement.SHINY_CHARM
        );
    }

    private static float getCharmMultiplier(
            ServerPlayer player,
            String slotIdentifier,
            Item charmItem,
            Function<ItemStack, Float> multiplierExtractor,
            ModAdvancement requiredAdvancement
    ) {
        return CuriosApi.getCuriosInventory(player)
                .map(inventory -> {
                    var slots = inventory.findCurios(slotIdentifier);
                    if (slots.isEmpty()) return 1.0f;

                    if (Config.CHARM_EFFECT_REQUIRES_ADVANCEMENT.get()) {
                        var advancement = requiredAdvancement.getAdvancement(player.serverLevel());
                        if (advancement == null) return 1.0f;
                        if (!player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                            return 1.0f;
                        }
                    }

                    float totalBonus = 0.0f;
                    for (var slotResult : slots) {
                        ItemStack stack = slotResult.stack();
                        if (!stack.isEmpty() && stack.is(charmItem)) {
                            totalBonus += (multiplierExtractor.apply(stack) - 1.0f);
                        }
                    }
                    return 1.0f + totalBonus;
                })
                .orElse(1.0f);
    }

    public static int countEquippedCharms(ServerPlayer player, String slotIdentifier, Item charmItem) {
        return CuriosApi.getCuriosInventory(player)
                .map(inventory -> {
                    int count = 0;
                    for (var slotResult : inventory.findCurios(slotIdentifier)) {
                        ItemStack stack = slotResult.stack();
                        if (!stack.isEmpty() && stack.is(charmItem)) count++;
                    }
                    return count;
                })
                .orElse(0);
    }
}