package com.darkbladenemo.cobblemoncharms.network;

import com.darkbladenemo.cobblemoncharms.cobblemoncharmsMod;
import com.darkbladenemo.cobblemoncharms.client.network.ClientPacketHandlers;
import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData;
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType;
import com.darkbladenemo.cobblemoncharms.network.payload.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = cobblemoncharmsMod.MOD_ID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                OpenMultiCharmScreenPayload.TYPE,
                OpenMultiCharmScreenPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        ClientPacketHandlers.handleOpenMultiCharmScreen(payload.slotIndex(), payload.fromCurio())
                )
        );

        registrar.playToServer(
                ToggleMultiCharmTypePayload.TYPE,
                ToggleMultiCharmTypePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    ItemStack stack = getMultiCharmStack(player, payload.curioSlotIndex(), payload.fromCurio());

                    if (!stack.isEmpty()) {
                        CharmType type = CharmType.fromString(payload.typeName());
                        if (type != null) {
                            MultiCharmData data = stack.get(ModDataComponents.MULTI_CHARM_DATA.get());
                            if (data != null && data.hasType(type)) {
                                MultiCharmData newData = data.toggleType(type);
                                stack.set(ModDataComponents.MULTI_CHARM_DATA.get(), newData);
                                PacketDistributor.sendToPlayer(player, new RefreshMultiCharmScreenPayload(newData));
                            }
                        }
                    }
                })
        );

        registrar.playToClient(
                RefreshMultiCharmScreenPayload.TYPE,
                RefreshMultiCharmScreenPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        ClientPacketHandlers.handleRefreshMultiCharmScreen(payload.data())
                )
        );

        registrar.playToServer(
                OpenMultiCharmFromCurioPayload.TYPE,
                OpenMultiCharmFromCurioPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();

                    CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                        var slots = inventory.findCurios("type_charm_slot");
                        int slotIndex = payload.slotIndex();

                        if (slotIndex >= 0 && slotIndex < slots.size()) {
                            ItemStack stack = slots.get(slotIndex).stack();
                            if (stack.is(ModItems.MULTI_CHARM.get())) {
                                PacketDistributor.sendToPlayer(player,
                                        new OpenMultiCharmScreenPayload(slotIndex, true));
                            }
                        }
                    });
                })
        );

        registrar.playToClient(
                SyncAdvancementsPayload.TYPE,
                SyncAdvancementsPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        com.darkbladenemo.cobblemoncharms.client.util.ClientAdvancementCache.INSTANCE
                                .update(payload.earnedIds())
                )
        );
    }

    /**
     * Resolves the Multi-Charm stack from either a Curios slot (keybind) or the player's hands
     * (shift-right-click).
     */
    private static ItemStack getMultiCharmStack(ServerPlayer player, int curioSlotIndex, boolean fromCurio) {
        if (fromCurio && curioSlotIndex >= 0) {
            ItemStack[] result = {ItemStack.EMPTY};
            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                var slots = inventory.findCurios("type_charm_slot");
                if (curioSlotIndex < slots.size()) {
                    result[0] = slots.get(curioSlotIndex).stack();
                }
            });
            return result[0];
        }

        if (player.getMainHandItem().is(ModItems.MULTI_CHARM.get())) {
            return player.getMainHandItem();
        } else if (player.getOffhandItem().is(ModItems.MULTI_CHARM.get())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }
}