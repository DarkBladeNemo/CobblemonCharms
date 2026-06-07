package com.darkbladenemo.cobblemoncharms.common.util;

import com.darkbladenemo.cobblemoncharms.advancement.ModAdvancement;
import com.darkbladenemo.cobblemoncharms.common.component.CatchCharmData;
import com.darkbladenemo.cobblemoncharms.common.component.ExpCharmData;
import com.darkbladenemo.cobblemoncharms.common.component.ShinyCharmData;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Function;

/**
 * Calculates charm multipliers from equipped Trinket items, with additive stacking.
 * Uses getInventory() + Container API (getContainerSize/getItem) under Mojang mappings.
 */
public class CharmMultiplierUtils {

    public static float getExpMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_EXP_CHARM.get()) return 1.0f;
        return getCharmMultiplier(
                player,
                ModItems.EXP_CHARM,
                stack -> {
                    ExpCharmData data = stack.get(ModDataComponents.EXP_CHARM_DATA);
                    return data != null ? data.multiplier()
                            : Config.EXP_CHARM_MULTIPLIER.floatValue();
                },
                ModAdvancement.EXP_CHARM
        );
    }

    public static float getCatchMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_CATCH_CHARM.get()) return 1.0f;
        return getCharmMultiplier(
                player,
                ModItems.CATCH_CHARM,
                stack -> {
                    CatchCharmData data = stack.get(ModDataComponents.CATCH_CHARM_DATA);
                    return data != null ? data.multiplier()
                            : Config.CATCH_CHARM_MULTIPLIER.floatValue();
                },
                ModAdvancement.CATCH_CHARM
        );
    }

    public static float getShinyMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_SHINY_CHARM.get()) return 1.0f;
        return getCharmMultiplier(
                player,
                ModItems.SHINY_CHARM,
                stack -> {
                    ShinyCharmData data = stack.get(ModDataComponents.SHINY_CHARM_DATA);
                    return data != null ? data.multiplier()
                            : Config.SHINY_CHARM_MULTIPLIER.floatValue();
                },
                ModAdvancement.SHINY_CHARM
        );
    }

    private static float getCharmMultiplier(
            ServerPlayer player,
            Item charmItem,
            Function<ItemStack, Float> multiplierExtractor,
            ModAdvancement requiredAdvancement
    ) {
        var optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isEmpty()) return 1.0f;

        if (Config.CHARM_EFFECT_REQUIRES_ADVANCEMENT.get()) {
            ServerLevel level = player.serverLevel();
            AdvancementHolder advancement = requiredAdvancement.getAdvancement(level);
            if (advancement == null) return 1.0f;
            if (!player.getAdvancements().getOrStartProgress(advancement).isDone()) return 1.0f;
        }

        float[] totalBonus = {0.0f};
        for (Map<String, TrinketInventory> slotGroup : optional.get().getInventory().values()) {
            for (TrinketInventory inv : slotGroup.values()) {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty() && stack.is(charmItem)) {
                        totalBonus[0] += (multiplierExtractor.apply(stack) - 1.0f);
                    }
                }
            }
        }

        return 1.0f + totalBonus[0];
    }

    public static int countEquippedCharms(ServerPlayer player, Item charmItem) {
        var optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isEmpty()) return 0;

        int[] count = {0};
        for (Map<String, TrinketInventory> slotGroup : optional.get().getInventory().values()) {
            for (TrinketInventory inv : slotGroup.values()) {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    if (inv.getItem(i).is(charmItem)) count[0]++;
                }
            }
        }
        return count[0];
    }
}