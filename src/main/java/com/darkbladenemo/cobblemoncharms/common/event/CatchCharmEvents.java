package com.darkbladenemo.cobblemoncharms.common.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.common.util.CharmMultiplierUtils;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

public class CatchCharmEvents {

    public static void register() {
        CobblemonEvents.POKEMON_CATCH_RATE.subscribe(Priority.NORMAL, event -> {
            handleCatchRate(event);
            return Unit.INSTANCE;
        });
    }

    private static void handleCatchRate(PokemonCatchRateEvent event) {
        if (!Config.ENABLE_CATCH_CHARM.get()) return;

        if (!(event.getThrower() instanceof ServerPlayer player)) return;

        float multiplier = CharmMultiplierUtils.getCatchMultiplier(player);

        if (multiplier <= 1.0f) return;

        event.setCatchRate(event.getCatchRate() * multiplier);
    }
}