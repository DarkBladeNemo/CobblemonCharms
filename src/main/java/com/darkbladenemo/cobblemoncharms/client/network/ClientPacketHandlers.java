package com.darkbladenemo.cobblemoncharms.client.network;

import com.darkbladenemo.cobblemoncharms.client.gui.MultiCharmScreen;
import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandlers {

    public static void handleOpenMultiCharmScreen(int slotIndex, boolean fromCurio) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.setScreen(new MultiCharmScreen(minecraft.player, slotIndex, fromCurio));
        }
    }

    /** No-op if the Multi-Charm screen is not currently open. */
    public static void handleRefreshMultiCharmScreen(MultiCharmData data) {
        if (Minecraft.getInstance().screen instanceof MultiCharmScreen screen) {
            screen.refreshData(data);
        }
    }

    public static void onDisconnect() {
        com.darkbladenemo.cobblemoncharms.client.util.ClientAdvancementCache.INSTANCE.clear();
    }
}