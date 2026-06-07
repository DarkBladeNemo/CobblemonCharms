package com.darkbladenemo.cobblemoncharms.client;

import com.darkbladenemo.cobblemoncharms.client.keybind.KeyInputHandler;
import com.darkbladenemo.cobblemoncharms.client.keybind.ModKeyBindings;
import com.darkbladenemo.cobblemoncharms.client.network.ClientPacketHandlers;
import com.darkbladenemo.cobblemoncharms.network.ModNetworkingClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class CobblemonCharmsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModNetworkingClient.registerClient();

        KeyBindingHelper.registerKeyBinding(ModKeyBindings.OPEN_MULTI_CHARM_GUI);
        KeyInputHandler.register();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                ClientPacketHandlers.onDisconnect()
        );
    }
}