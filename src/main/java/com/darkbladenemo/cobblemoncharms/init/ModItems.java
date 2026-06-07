package com.darkbladenemo.cobblemoncharms.init;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.darkbladenemo.cobblemoncharms.common.component.*;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.common.item.EVBoostItem;
import com.darkbladenemo.cobblemoncharms.common.item.IVBoostItem;
import com.darkbladenemo.cobblemoncharms.common.item.charm.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ModItems {

    public static final Map<CharmType, TypeCharm> TYPE_CHARMS = new EnumMap<>(CharmType.class);

    // EV Items
    public static EVBoostItem SUPER_CARBOS;
    public static EVBoostItem SUPER_PROTEIN;
    public static EVBoostItem SUPER_HP_UP;
    public static EVBoostItem SUPER_IRON;
    public static EVBoostItem SUPER_CALCIUM;
    public static EVBoostItem SUPER_ZINC;

    // IV Items
    public static IVBoostItem SUPER_HEALTH_CANDY;
    public static IVBoostItem SUPER_MIGHTY_CANDY;
    public static IVBoostItem SUPER_TOUGH_CANDY;
    public static IVBoostItem SUPER_SMART_CANDY;
    public static IVBoostItem SUPER_COURAGE_CANDY;
    public static IVBoostItem SUPER_QUICK_CANDY;
    public static IVBoostItem GOLD_BOTTLE_CAP;

    // Charms
    public static ShinyCharm SHINY_CHARM;
    public static ExpCharm   EXP_CHARM;
    public static CatchCharm CATCH_CHARM;
    public static MultiCharm MULTI_CHARM;

    public static void register() {
        // Read config values once
        float expMultiplier       = Config.SPEC.isLoaded() ? Config.EXP_CHARM_MULTIPLIER.floatValue()        : 1.5f;
        float catchMultiplier     = Config.SPEC.isLoaded() ? Config.CATCH_CHARM_MULTIPLIER.floatValue()      : 1.5f;
        float shinyMultiplier     = Config.SPEC.isLoaded() ? Config.SHINY_CHARM_MULTIPLIER.floatValue()      : 3.0f;
        float typeMatchMultiplier = Config.SPEC.isLoaded() ? Config.TYPE_CHARM_MATCH_MULTIPLIER.floatValue() : 5.0f;
        int   evAmount            = Config.SPEC.isLoaded() ? Config.SUPER_EV_INCREASE_AMOUNT.get()                 : 100;
        int   ivAmount            = Config.SPEC.isLoaded() ? Config.SUPER_IV_INCREASE_AMOUNT.get()                 : 10;
        int   goldCapAmount       = Config.SPEC.isLoaded() ? Config.GOLD_BOTTLE_CAP_IV_AMOUNT.get()                : 31;

        // EV Items
        SUPER_CARBOS  = register("super_carbos",  new EVBoostItem(Stats.SPEED,
                () -> Config.ENABLE_ALL_EV_ITEMS.get() && Config.ENABLE_SUPER_CARBOS.get(),
                new Item.Properties().component(ModDataComponents.EV_ITEM_DATA, new EVItemData("speed",           evAmount))));
        SUPER_PROTEIN = register("super_protein", new EVBoostItem(Stats.ATTACK,
                () -> Config.ENABLE_ALL_EV_ITEMS.get() && Config.ENABLE_SUPER_PROTEIN.get(),
                new Item.Properties().component(ModDataComponents.EV_ITEM_DATA, new EVItemData("attack",          evAmount))));
        SUPER_HP_UP   = register("super_hp_up",   new EVBoostItem(Stats.HP,
                () -> Config.ENABLE_ALL_EV_ITEMS.get() && Config.ENABLE_SUPER_HP_UP.get(),
                new Item.Properties().component(ModDataComponents.EV_ITEM_DATA, new EVItemData("hp",              evAmount))));
        SUPER_IRON    = register("super_iron",     new EVBoostItem(Stats.DEFENCE,
                () -> Config.ENABLE_ALL_EV_ITEMS.get() && Config.ENABLE_SUPER_IRON.get(),
                new Item.Properties().component(ModDataComponents.EV_ITEM_DATA, new EVItemData("defence",         evAmount))));
        SUPER_CALCIUM = register("super_calcium",  new EVBoostItem(Stats.SPECIAL_ATTACK,
                () -> Config.ENABLE_ALL_EV_ITEMS.get() && Config.ENABLE_SUPER_CALCIUM.get(),
                new Item.Properties().component(ModDataComponents.EV_ITEM_DATA, new EVItemData("special_attack",  evAmount))));
        SUPER_ZINC    = register("super_zinc",     new EVBoostItem(Stats.SPECIAL_DEFENCE,
                () -> Config.ENABLE_ALL_EV_ITEMS.get() && Config.ENABLE_SUPER_ZINC.get(),
                new Item.Properties().component(ModDataComponents.EV_ITEM_DATA, new EVItemData("special_defence", evAmount))));

        // IV Items
        SUPER_HEALTH_CANDY  = register("super_health_candy",  new IVBoostItem(Set.of(Stats.HP),
                () -> Config.ENABLE_ALL_IV_ITEMS.get() && Config.ENABLE_SUPER_HEALTH_CANDY.get(),
                new Item.Properties().component(ModDataComponents.IV_ITEM_DATA, new IVItemData(java.util.List.of("hp"),              ivAmount))));
        SUPER_MIGHTY_CANDY  = register("super_mighty_candy",  new IVBoostItem(Set.of(Stats.ATTACK),
                () -> Config.ENABLE_ALL_IV_ITEMS.get() && Config.ENABLE_SUPER_MIGHTY_CANDY.get(),
                new Item.Properties().component(ModDataComponents.IV_ITEM_DATA, new IVItemData(java.util.List.of("attack"),          ivAmount))));
        SUPER_TOUGH_CANDY   = register("super_tough_candy",   new IVBoostItem(Set.of(Stats.DEFENCE),
                () -> Config.ENABLE_ALL_IV_ITEMS.get() && Config.ENABLE_SUPER_TOUGH_CANDY.get(),
                new Item.Properties().component(ModDataComponents.IV_ITEM_DATA, new IVItemData(java.util.List.of("defence"),         ivAmount))));
        SUPER_SMART_CANDY   = register("super_smart_candy",   new IVBoostItem(Set.of(Stats.SPECIAL_ATTACK),
                () -> Config.ENABLE_ALL_IV_ITEMS.get() && Config.ENABLE_SUPER_SMART_CANDY.get(),
                new Item.Properties().component(ModDataComponents.IV_ITEM_DATA, new IVItemData(java.util.List.of("special_attack"),  ivAmount))));
        SUPER_COURAGE_CANDY = register("super_courage_candy", new IVBoostItem(Set.of(Stats.SPECIAL_DEFENCE),
                () -> Config.ENABLE_ALL_IV_ITEMS.get() && Config.ENABLE_SUPER_COURAGE_CANDY.get(),
                new Item.Properties().component(ModDataComponents.IV_ITEM_DATA, new IVItemData(java.util.List.of("special_defence"), ivAmount))));
        SUPER_QUICK_CANDY   = register("super_quick_candy",   new IVBoostItem(Set.of(Stats.SPEED),
                () -> Config.ENABLE_ALL_IV_ITEMS.get() && Config.ENABLE_SUPER_QUICK_CANDY.get(),
                new Item.Properties().component(ModDataComponents.IV_ITEM_DATA, new IVItemData(java.util.List.of("speed"),           ivAmount))));
        GOLD_BOTTLE_CAP     = register("gold_bottle_cap",     new IVBoostItem(
                Set.of(Stats.HP, Stats.ATTACK, Stats.DEFENCE, Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED),
                () -> Config.ENABLE_ALL_IV_ITEMS.get() && Config.ENABLE_GOLD_BOTTLE_CAP.get(),
                new Item.Properties().component(ModDataComponents.IV_ITEM_DATA, new IVItemData(
                        java.util.List.of("hp", "attack", "defence", "special_attack", "special_defence", "speed"),
                        goldCapAmount))));

        // Charms
        SHINY_CHARM = register("shiny_charm", new ShinyCharm(
                new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                        .component(ModDataComponents.SHINY_CHARM_DATA, new ShinyCharmData(shinyMultiplier))));
        EXP_CHARM   = register("exp_charm",   new ExpCharm(
                new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                        .component(ModDataComponents.EXP_CHARM_DATA, new ExpCharmData(expMultiplier))));
        CATCH_CHARM = register("catch_charm", new CatchCharm(
                new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                        .component(ModDataComponents.CATCH_CHARM_DATA, new CatchCharmData(catchMultiplier))));
        MULTI_CHARM = register("multi_charm", new MultiCharm(
                new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                        .component(ModDataComponents.MULTI_CHARM_DATA, MultiCharmData.empty())));

        // Type charms
        for (CharmType type : CharmType.getEntries()) {
            String name = type.getTranslationKey() + "_charm";
            TypeCharm charm = register(name, new TypeCharm(type,
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                            .component(ModDataComponents.TYPE_CHARM_DATA,
                                    new TypeCharmData(type, typeMatchMultiplier))));
            TYPE_CHARMS.put(type, charm);
        }
    }

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath("cobblemoncharms", name),
                item
        );
    }
}