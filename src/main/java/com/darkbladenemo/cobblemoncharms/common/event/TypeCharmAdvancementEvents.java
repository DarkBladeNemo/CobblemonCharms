package com.darkbladenemo.cobblemoncharms.common.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.darkbladenemo.cobblemoncharms.advancement.ModAdvancement;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType;
import com.darkbladenemo.cobblemoncharms.common.tracking.TypeCharmProgressTracker;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import com.darkbladenemo.cobblemoncharms.utils.AdvancementUtils;
import com.darkbladenemo.cobblemoncharms.utils.ItemGiveUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import kotlin.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * Listens for Pokédex catches and grants the appropriate type charm advancement and item
 * reward when a player reaches the configured catch threshold for a given type.
 * Also invalidates the per-player progress cache on logout.
 */
public class TypeCharmAdvancementEvents {

    public static void register() {
        CobblemonEvents.POKEDEX_DATA_CHANGED_POST.subscribe(Priority.NORMAL, event -> {
            handlePokedexChanged(event);
            return Unit.INSTANCE;
        });

        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                checkAllTypeCharmsForPlayer(player);
            }
        });

        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                TypeCharmProgressTracker.invalidate(player.getUUID());
            }
        });
    }

    public static void checkAllTypeCharmsForPlayer(ServerPlayer player) {
        if (!Config.ENABLE_ALL_TYPE_CHARMS.get()) return;
        var server = player.server;
        if (server == null) return;

        double threshold = Config.TYPE_CHARM_THRESHOLD_PERCENTAGE.get();
        for (CharmType type : CharmType.getEntries()) {
            tryGrantTypeCharm(player, server, type, threshold);
        }
    }

    private static void handlePokedexChanged(PokedexDataChangedEvent.Post event) {
        if (!TypeCharmProgressTracker.meetsRequiredKnowledge(event.getKnowledge())) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        ServerPlayer player = server.getPlayerList().getPlayer(event.getPlayerUUID());
        if (player == null) return;

        if (!Config.ENABLE_ALL_TYPE_CHARMS.get()) return;

        Pokemon pokemon = event.getDataSource().getPokemon();
        FormDexRecord formRecord = event.getRecord();

        boolean anyNew = TypeCharmProgressTracker.record(player, pokemon, formRecord);
        if (!anyNew) return;

        double threshold = Config.TYPE_CHARM_THRESHOLD_PERCENTAGE.get();
        for (CharmType type : getTypesForForm(pokemon, formRecord)) {
            tryGrantTypeCharm(player, server, type, threshold);
        }
    }

    /**
     * Checks progress for a single type and grants the advancement + item reward
     * if the player has met the threshold. No-op if the charm type is disabled,
     * the threshold isn't met yet, or the advancement was already earned.
     */
    private static void tryGrantTypeCharm(ServerPlayer player, net.minecraft.server.MinecraftServer server,
                                          CharmType type, double threshold) {
        if (!Config.isTypeCharmEnabled(type)) return;

        int required = TypeCharmProgressTracker.computeThreshold(type, threshold);
        int current = TypeCharmProgressTracker.getUniqueCount(player, type);
        if (current < required) return;

        AdvancementHolder advancement = ModAdvancement.getTypeCharmAdvancement(server, type);
        if (advancement == null) return;

        boolean granted = AdvancementUtils.grantAdvancement(player, advancement);
        if (!granted) return;
        if (!Config.isTypeCharmGrantedOnAdvancement(type)) return;

        var charmHolder = ModItems.TYPE_CHARMS.get(type);
        if (charmHolder == null) return;

        ItemGiveUtils.giveOrDrop(player, new ItemStack(charmHolder.get()),
                "message.cobblemoncharms.type_charm_awarded",
                Component.translatable("cobblemon.type." + type.getTranslationKey()));
    }

    private static List<CharmType> getTypesForForm(Pokemon pokemon, FormDexRecord formRecord) {
        List<CharmType> types = new ArrayList<>();
        FormData form = pokemon.getSpecies().getFormByName(formRecord.getFormName());

        CharmType primary = CharmType.fromElementalType(form.getPrimaryType());
        if (primary != null) types.add(primary);

        var secondary = form.getSecondaryType();
        if (secondary != null) {
            CharmType ct = CharmType.fromElementalType(secondary);
            if (ct != null) types.add(ct);
        }
        return types;
    }
}