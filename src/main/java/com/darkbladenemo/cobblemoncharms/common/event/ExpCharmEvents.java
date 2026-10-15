package com.darkbladenemo.cobblemoncharms.common.event;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.pokemon.experience.BattleExperienceSource;
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceSource;
import com.darkbladenemo.cobblemoncharms.advancement.ModAdvancement;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.common.util.CharmMultiplierUtils;
import com.darkbladenemo.cobblemoncharms.utils.AdvancementUtils;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

public class ExpCharmEvents {

    public static void register() {
        CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, event -> {
            handleExpEvent(event);
            return Unit.INSTANCE;
        });

        CobblemonEvents.EXPERIENCE_GAINED_EVENT_POST.subscribe(Priority.NORMAL, event -> {
            handleLevelReached(event);
            return Unit.INSTANCE;
        });
    }

    /** Battle XP is always boosted; candy/command/sidemod XP only if the config allows it. */
    private static boolean isBoostedSource(ExperienceSource source) {
        return source instanceof BattleExperienceSource || Config.EXP_CHARM_AFFECTS_ALL_SOURCES.get();
    }

    private static void handleExpEvent(ExperienceGainedEvent.Pre event) {
        if (!isBoostedSource(event.getSource())) return;

        ServerPlayer player = event.getPokemon().getOwnerPlayer();
        if (player == null) return;

        float multiplier = CharmMultiplierUtils.getExpMultiplier(player);
        if (multiplier <= 1.0f) return;

        int originalExp = event.getExperience();
        int newExp = Math.round(originalExp * multiplier);
        if (newExp <= 0 && originalExp > 0) newExp = 1;

        event.setExperience(newExp);
    }

    /** Fires after the level is applied, for every XP source, so any level-up counts toward the advancement. */
    private static void handleLevelReached(ExperienceGainedEvent.Post event) {
        if (!Config.ENABLE_EXP_CHARM.get()) return;

        int required = Math.min(
                Config.EXP_CHARM_REQUIRED_LEVEL.get(),
                Cobblemon.INSTANCE.getConfig().getMaxPokemonLevel());
        if (event.getCurrentLevel() < required) return;

        ServerPlayer player = event.getPokemon().getOwnerPlayer();
        if (player == null) return;

        AdvancementUtils.grantAdvancement(player, ModAdvancement.EXP_CHARM.getAdvancement(player.serverLevel()));
    }
}