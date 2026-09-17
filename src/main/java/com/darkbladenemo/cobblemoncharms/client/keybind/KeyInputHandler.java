package com.darkbladenemo.cobblemoncharms.client.keybind;

import com.darkbladenemo.cobblemoncharms.client.gui.MultiCharmSelectionScreen;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import com.darkbladenemo.cobblemoncharms.network.payload.OpenMultiCharmFromCurioPayload;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;

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
            AccessoriesCapability capability = AccessoriesCapability.get(player);
            if (capability != null) {
                for (SlotEntryReference ref : capability.getEquipped(ModItems.MULTI_CHARM)) {
                    multiCharmSlots.add(ref.reference().slot());
                }
            }

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