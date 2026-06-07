package com.darkbladenemo.cobblemoncharms.client.keybind;

import com.darkbladenemo.cobblemoncharms.client.gui.MultiCharmSelectionScreen;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import com.darkbladenemo.cobblemoncharms.network.payload.OpenMultiCharmFromCurioPayload;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class KeyInputHandler {

    /**
     * Registers the key input tick listener.
     * Call from CobblemonCharmsClientFabric.onInitializeClient().
     */
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ModKeyBindings.OPEN_MULTI_CHARM_GUI.consumeClick()) return;

            LocalPlayer player = client.player;
            if (player == null) return;

            List<Integer> multiCharmSlots = new ArrayList<>();

            TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
                var slots = trinkets.getEquipped(item -> item.is(ModItems.MULTI_CHARM));
                for (int i = 0; i < slots.size(); i++) {
                    multiCharmSlots.add(i);
                }
            });

            if (multiCharmSlots.isEmpty()) return;

            if (multiCharmSlots.size() == 1) {
                ClientPlayNetworking.send(
                        new OpenMultiCharmFromCurioPayload(multiCharmSlots.getFirst()));
            } else {
                client.setScreen(new MultiCharmSelectionScreen(player, multiCharmSlots));
            }
        });
    }
}