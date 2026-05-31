package com.darkbladenemo.cobblemoncharms.command;

import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType;
import com.darkbladenemo.cobblemoncharms.common.tracking.TypeCharmProgressTracker;
import com.darkbladenemo.cobblemoncharms.common.util.CharmMultiplierUtils;
import com.darkbladenemo.cobblemoncharms.utils.PokedexRegionUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CCharmsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ccharms")
                        .then(Commands.literal("dex")
                                .executes(CCharmsCommand::dex)
                        )
                        .then(Commands.literal("type")
                                .executes(CCharmsCommand::type)
                        )
                        .then(Commands.literal("debug")
                                .then(Commands.literal("shiny")
                                        .executes(CCharmsCommand::debugShiny)
                                )
                        )
        );
    }

    private static int dex(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Must be run as a player."));
            return 0;
        }

        PokedexRegionUtils.RegionProgress progress =
                PokedexRegionUtils.getRegionProgress(player, "national");

        player.sendSystemMessage(Component.literal("§6=== Pokédex Progress ==="));

        if (progress == null) {
            player.sendSystemMessage(Component.literal("§cCould not retrieve Pokédex progress."));
            return 0;
        }

        double threshold = Config.SHINY_CHARM_DEX_THRESHOLD.get();
        int needed = (int) Math.ceil(progress.totalImplemented() * threshold / 100.0);
        int remaining = Math.max(0, needed - progress.totalCaught());

        player.sendSystemMessage(Component.literal(
                "§7Caught: §a" + progress.totalCaught() + " §7/ §a" + progress.totalImplemented()
        ));
        player.sendSystemMessage(Component.literal(
                "§7Seen: §e" + progress.totalSeen()
        ));
        player.sendSystemMessage(Component.literal(
                "§7Completion: §a" + String.format("%.2f", progress.completionPercentage()) + "%"
        ));

        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(Component.literal(
                "§7Shiny Charm Threshold: §e" + String.format("%.1f", threshold) + "% §7(§a" + needed + " §7needed)"
        ));

        if (remaining == 0) {
            player.sendSystemMessage(Component.literal("§aStatus: §2✓ Threshold reached"));
        } else {
            player.sendSystemMessage(Component.literal(
                    "§7Status: In Progress — §c" + remaining + " §7more to go"
            ));
        }

        return 1;
    }

    private static int type(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Must be run as a player."));
            return 0;
        }

        double percentage = Config.TYPE_CHARM_THRESHOLD_PERCENTAGE.get();

        player.sendSystemMessage(Component.literal(
                "§6=== Type Charm Progress §7(threshold: §e" + percentage + "%§7) ==="
        ));

        for (CharmType type : CharmType.getEntries()) {
            int caught = TypeCharmProgressTracker.getUniqueCount(player, type);
            int implemented = TypeCharmProgressTracker.getImplementedCount(type);
            int threshold = TypeCharmProgressTracker.computeThreshold(type, percentage);
            boolean done = caught >= threshold;

            String typeName = type.getTranslationKey().substring(0, 1).toUpperCase()
                    + type.getTranslationKey().substring(1);

            String line = done
                    ? "§7" + typeName + " - §aUnlocked"
                    : "§7" + typeName + " - §e" + caught + " / " + threshold
                      + " §8(" + implemented + " implemented)";

            player.sendSystemMessage(Component.literal(line));
        }

        return 1;
    }

    private static int debugShiny(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Must be run as a player."));
            return 0;
        }

        float multiplier = CharmMultiplierUtils.getShinyMultiplier(player);
        int charmsEquipped = CharmMultiplierUtils.countEquippedCharms(
                player, "shiny_charm_slot",
                com.darkbladenemo.cobblemoncharms.init.ModItems.SHINY_CHARM.get()
        );

        player.sendSystemMessage(Component.literal("§6=== Shiny Charm Debug ==="));
        player.sendSystemMessage(Component.literal("§7Charms equipped: §e" + charmsEquipped));
        player.sendSystemMessage(Component.literal("§7Effective multiplier: §a" + multiplier + "x"));

        if (multiplier <= 1.0f) {
            player.sendSystemMessage(Component.literal(
                    "§cNo active bonus — charm not equipped or advancement not earned."
            ));
        } else {
            int effectiveOdds = Math.round(8192 / multiplier);
            player.sendSystemMessage(Component.literal(
                    "§7Effective shiny rate: §a1 in " + effectiveOdds + " §8(base 1 in 8192)"
            ));
        }

        return 1;
    }
}