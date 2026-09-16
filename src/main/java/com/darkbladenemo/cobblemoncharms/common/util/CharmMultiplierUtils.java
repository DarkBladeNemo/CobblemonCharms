package com.darkbladenemo.cobblemoncharms.common.util;

import com.darkbladenemo.cobblemoncharms.advancement.ModAdvancement;
import com.darkbladenemo.cobblemoncharms.common.component.CatchCharmData;
import com.darkbladenemo.cobblemoncharms.common.component.ExpCharmData;
import com.darkbladenemo.cobblemoncharms.common.component.ShinyCharmData;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * Calculates charm multipliers from equipped Accessories items, with additive stacking
 * across multiple charms of the same type.
 */
public class CharmMultiplierUtils {

    public static float getExpMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_EXP_CHARM.get()) return 1.0f;
        return getCharmMultiplier(player, ModItems.EXP_CHARM.get(),
                stack -> {
                    ExpCharmData data = stack.get(ModDataComponents.EXP_CHARM_DATA.get());
                    return data != null ? data.multiplier() : Config.EXP_CHARM_MULTIPLIER.get().floatValue();
                },
                ModAdvancement.EXP_CHARM);
    }

    public static float getCatchMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_CATCH_CHARM.get()) return 1.0f;
        return getCharmMultiplier(player, ModItems.CATCH_CHARM.get(),
                stack -> {
                    CatchCharmData data = stack.get(ModDataComponents.CATCH_CHARM_DATA.get());
                    return data != null ? data.multiplier() : Config.CATCH_CHARM_MULTIPLIER.get().floatValue();
                },
                ModAdvancement.CATCH_CHARM);
    }

    public static float getShinyMultiplier(ServerPlayer player) {
        if (!Config.ENABLE_SHINY_CHARM.get()) return 1.0f;
        return getCharmMultiplier(player, ModItems.SHINY_CHARM.get(),
                stack -> {
                    ShinyCharmData data = stack.get(ModDataComponents.SHINY_CHARM_DATA.get());
                    return data != null ? data.multiplier() : Config.SHINY_CHARM_MULTIPLIER.get().floatValue();
                },
                ModAdvancement.SHINY_CHARM);
    }

    private static float getCharmMultiplier(
            ServerPlayer player,
            Item charmItem,
            Function<ItemStack, Float> multiplierExtractor,
            ModAdvancement requiredAdvancement
    ) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) return 1.0f;

        var equipped = capability.getEquipped(charmItem);
        if (equipped.isEmpty()) return 1.0f;

        if (Config.CHARM_EFFECT_REQUIRES_ADVANCEMENT.get()) {
            var advancement = requiredAdvancement.getAdvancement(player.serverLevel());
            if (advancement == null) return 1.0f;
            if (!player.getAdvancements().getOrStartProgress(advancement).isDone()) return 1.0f;
        }

        float totalBonus = 0.0f;
        for (SlotEntryReference entry : equipped) {
            totalBonus += (multiplierExtractor.apply(entry.stack()) - 1.0f);
        }
        return 1.0f + totalBonus;
    }

    public static int countEquippedCharms(ServerPlayer player, Item charmItem) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        return capability == null ? 0 : capability.getEquipped(charmItem).size();
    }
}