package com.darkbladenemo.cobblemoncharms.network;

import com.darkbladenemo.cobblemoncharms.CobblemonCharmsMod;
import com.darkbladenemo.cobblemoncharms.client.network.ClientPacketHandlers;
import com.darkbladenemo.cobblemoncharms.common.component.MultiCharmData;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.init.ModDataComponents;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType;
import com.darkbladenemo.cobblemoncharms.network.payload.*;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CobblemonCharmsMod.MOD_ID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                OpenMultiCharmScreenPayload.TYPE,
                OpenMultiCharmScreenPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        ClientPacketHandlers.handleOpenMultiCharmScreen(
                                payload.slotIndex(), payload.fromCurio())
                )
        );

        registrar.playToServer(
                ToggleMultiCharmTypePayload.TYPE,
                ToggleMultiCharmTypePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    ItemStack stack = getMultiCharmStack(
                            player, payload.curioSlotIndex(), payload.fromCurio());

                    if (!stack.isEmpty()) {
                        CharmType type = CharmType.fromString(payload.typeName());
                        if (type != null) {
                            MultiCharmData data = stack.get(ModDataComponents.MULTI_CHARM_DATA.get());
                            if (data != null && data.hasType(type)) {
                                MultiCharmData newData = data.toggleType(type);
                                stack.set(ModDataComponents.MULTI_CHARM_DATA.get(), newData);
                                PacketDistributor.sendToPlayer(player,
                                        new RefreshMultiCharmScreenPayload(newData));
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

                    AccessoriesCapability capability = AccessoriesCapability.get(player);
                    var container = capability != null ? capability.getContainers().get("type_charm_slot") : null;
                    int slotIndex = payload.slotIndex();

                    if (container != null && slotIndex >= 0 && slotIndex < container.getSize()) {
                        ItemStack stack = container.getAccessories().getItem(slotIndex);
                        if (stack.is(ModItems.MULTI_CHARM.get())) {
                            PacketDistributor.sendToPlayer(player,
                                    new OpenMultiCharmScreenPayload(slotIndex, true));
                        }
                    }
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

        registrar.playToClient(
                SyncConfigPayload.TYPE,
                SyncConfigPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        Config.syncFromServer(
                                payload.charmEffectRequiresAdvancement(),
                                payload.grantCharmOnAdvancement(),
                                payload.shinyCharmMultiplier(),
                                payload.expCharmMultiplier(),
                                payload.catchCharmMultiplier(),
                                payload.typeCharmMatchMultiplier(),
                                payload.typeCharmNonMatchMultiplier(),
                                payload.typeCharmRadius(),
                                payload.typeCharmThresholdPercentage()
                        )
                )
        );
    }

    public static SyncConfigPayload buildConfigSnapshot() {
        return new SyncConfigPayload(
                Config.CHARM_EFFECT_REQUIRES_ADVANCEMENT.get(),
                Config.GRANT_CHARM_ON_ADVANCEMENT.get(),
                Config.SHINY_CHARM_MULTIPLIER.get().floatValue(),
                Config.EXP_CHARM_MULTIPLIER.get().floatValue(),
                Config.CATCH_CHARM_MULTIPLIER.get().floatValue(),
                Config.TYPE_CHARM_MATCH_MULTIPLIER.get().floatValue(),
                Config.TYPE_CHARM_NON_MATCH_MULTIPLIER.get().floatValue(),
                Config.TYPE_CHARM_RADIUS.get(),
                Config.TYPE_CHARM_THRESHOLD_PERCENTAGE.get()
        );
    }

    private static ItemStack getMultiCharmStack(ServerPlayer player,
                                                int curioSlotIndex, boolean fromCurio) {
        if (fromCurio && curioSlotIndex >= 0) {
            AccessoriesCapability capability = AccessoriesCapability.get(player);
            var container = capability != null ? capability.getContainers().get("type_charm_slot") : null;
            if (container != null && curioSlotIndex < container.getSize()) {
                return container.getAccessories().getItem(curioSlotIndex);
            }
            return ItemStack.EMPTY;
        }

        if (player.getMainHandItem().is(ModItems.MULTI_CHARM.get())) {
            return player.getMainHandItem();
        } else if (player.getOffhandItem().is(ModItems.MULTI_CHARM.get())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }
}