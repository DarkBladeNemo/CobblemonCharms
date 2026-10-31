package com.darkbladenemo.cobblemoncharms.datagen;

import com.cobblemon.mod.common.CobblemonItems;
import com.darkbladenemo.cobblemoncharms.CobblemonCharmsFabric;
import com.darkbladenemo.cobblemoncharms.common.config.Config;
import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    private static final Map<CharmType, Item> TYPE_MATERIALS = Map.ofEntries(
            Map.entry(CharmType.NORMAL,   CobblemonItems.NORMAL_GEM),
            Map.entry(CharmType.FIRE,     CobblemonItems.FIRE_GEM),
            Map.entry(CharmType.WATER,    CobblemonItems.WATER_GEM),
            Map.entry(CharmType.ELECTRIC, CobblemonItems.ELECTRIC_GEM),
            Map.entry(CharmType.GRASS,    CobblemonItems.GRASS_GEM),
            Map.entry(CharmType.ICE,      CobblemonItems.ICE_GEM),
            Map.entry(CharmType.FIGHTING, CobblemonItems.FIGHTING_GEM),
            Map.entry(CharmType.POISON,   CobblemonItems.POISON_GEM),
            Map.entry(CharmType.GROUND,   CobblemonItems.GROUND_GEM),
            Map.entry(CharmType.FLYING,   CobblemonItems.FLYING_GEM),
            Map.entry(CharmType.PSYCHIC,  CobblemonItems.PSYCHIC_GEM),
            Map.entry(CharmType.BUG,      CobblemonItems.BUG_GEM),
            Map.entry(CharmType.ROCK,     CobblemonItems.ROCK_GEM),
            Map.entry(CharmType.GHOST,    CobblemonItems.GHOST_GEM),
            Map.entry(CharmType.DRAGON,   CobblemonItems.DRAGON_GEM),
            Map.entry(CharmType.DARK,     CobblemonItems.DARK_GEM),
            Map.entry(CharmType.STEEL,    CobblemonItems.STEEL_GEM),
            Map.entry(CharmType.FAIRY,    CobblemonItems.FAIRY_GEM)
    );

    public ModRecipeProvider(FabricDataOutput output,
                             CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {

        // Gold Bottle Cap
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_BOTTLE_CAP)
                .pattern("GGG")
                .pattern("GNG")
                .pattern("GGG")
                .define('G', Items.GOLD_INGOT)
                .define('N', Items.NETHERITE_INGOT)
                .unlockedBy("has_netherite",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(output, id("candies/gold_bottle_cap"));

        // Charms
        if (Config.ENABLE_SHINY_CHARM.get()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SHINY_CHARM)
                    .pattern("ACD")
                    .pattern("CBC")
                    .pattern("DCB")
                    .define('A', Items.LEAD)
                    .define('B', CobblemonItems.WATER_GEM)
                    .define('C', Items.GOLD_INGOT)
                    .define('D', Items.PRISMARINE_CRYSTALS)
                    .unlockedBy("has_water_gem",
                            InventoryChangeTrigger.TriggerInstance.hasItems(CobblemonItems.WATER_GEM))
                    .save(output, id("charms/shiny_charm"));
        }

        if (Config.ENABLE_EXP_CHARM.get()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EXP_CHARM)
                    .pattern("ABE")
                    .pattern("BCB")
                    .pattern("EBD")
                    .define('A', Items.CHAIN)
                    .define('B', CobblemonItems.WATER_GEM)
                    .define('C', Items.NETHER_STAR)
                    .define('E', Items.EXPERIENCE_BOTTLE)
                    .define('D', Items.BLUE_DYE)
                    .unlockedBy("has_nether_star",
                            InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHER_STAR))
                    .save(output, id("charms/exp_charm"));
        }

        if (Config.ENABLE_CATCH_CHARM.get()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CATCH_CHARM)
                    .pattern("ADA")
                    .pattern("CBE")
                    .pattern("AFA")
                    .define('A', Items.GOLD_INGOT)
                    .define('B', CobblemonItems.POKE_BALL)
                    .define('C', CobblemonItems.WATER_GEM)
                    .define('D', CobblemonItems.GROUND_GEM)
                    .define('E', CobblemonItems.FLYING_GEM)
                    .define('F', CobblemonItems.DRAGON_GEM)
                    .unlockedBy("has_poke_ball",
                            InventoryChangeTrigger.TriggerInstance.hasItems(CobblemonItems.POKE_BALL))
                    .save(output, id("charms/catch_charm"));
        }

        if (Config.ENABLE_MULTI_CHARM.get()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MULTI_CHARM)
                    .pattern("GDG")
                    .pattern("CEA")
                    .pattern("GBG")
                    .define('G', Items.GOLD_INGOT)
                    .define('E', Items.NETHER_STAR)
                    .define('C', CobblemonItems.FIRE_GEM)
                    .define('A', CobblemonItems.WATER_GEM)
                    .define('B', CobblemonItems.GRASS_GEM)
                    .define('D', CobblemonItems.GHOST_GEM)
                    .unlockedBy("has_nether_star",
                            InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHER_STAR))
                    .save(output, id("charms/multi_charm"));
        }

        // Multi charm combining recipe — always registered if multi charm is enabled
        if (Config.ENABLE_MULTI_CHARM.get()) {
            output.accept(
                    id("multi_charm_crafting"),
                    new com.darkbladenemo.cobblemoncharms.common.recipe.MultiCharmRecipe(
                            CraftingBookCategory.MISC),
                    null
            );
        }

        // Type Charms
        if (Config.ENABLE_ALL_TYPE_CHARMS.get()) {
            TYPE_MATERIALS.forEach((type, material) -> {
                if (!Config.isTypeCharmEnabled(type)) return;
                Item charm = ModItems.TYPE_CHARMS.get(type);
                if (charm == null) return;
                String key = type.getTranslationKey();
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, charm)
                        .pattern(" T ")
                        .pattern("TGT")
                        .pattern("TTT")
                        .define('T', Items.GOLD_INGOT)
                        .define('G', material)
                        .unlockedBy("has_material",
                                InventoryChangeTrigger.TriggerInstance.hasItems(material))
                        .save(output, id("charms/" + key + "_charm"));
            });
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(CobblemonCharmsFabric.MOD_ID, path);
    }
}