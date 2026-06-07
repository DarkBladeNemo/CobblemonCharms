package com.darkbladenemo.cobblemoncharms.client.network;

import com.darkbladenemo.cobblemoncharms.client.gui.MultiCharmScreen;
import com.darkbladenemo.cobblemoncharms.client.util.ClientAdvancementCache;
import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class ClientPacketHandlers {

    public static void handleOpenMultiCharmScreen(int slotIndex, boolean fromCurio) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.setScreen(new MultiCharmScreen(minecraft.player, slotIndex, fromCurio));
        }
    }

    public static void handleRefreshMultiCharmScreen(MultiCharmData data) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof MultiCharmScreen screen) {
            screen.refreshData(data);
        }
    }

    public static void onDisconnect() {
        ClientAdvancementCache.INSTANCE.clear();
    }
}