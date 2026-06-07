package com.darkbladenemo.cobblemoncharms.tick;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TickManager {

    /** Registers the server tick listener. Call from CobblemonCharmsFabric.onInitialize(). */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Check once per second (every 20 ticks) to keep overhead low
            if (server.getTickCount() % 20 == 0) {
                HandleAdvancement.checkAdvancements(server);
            }
        });
    }
}