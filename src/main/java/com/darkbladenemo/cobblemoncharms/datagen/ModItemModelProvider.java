package com.darkbladenemo.cobblemoncharms.datagen;

import com.darkbladenemo.cobblemoncharms.common.item.charm.CharmType;
import com.darkbladenemo.cobblemoncharms.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class ModItemModelProvider extends FabricModelProvider {

    public ModItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        // No block models needed
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(ModItems.SUPER_CARBOS,       ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_PROTEIN,      ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_HP_UP,        ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_IRON,         ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_CALCIUM,      ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_ZINC,         ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModItems.SUPER_HEALTH_CANDY,  ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_MIGHTY_CANDY,  ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_TOUGH_CANDY,   ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_SMART_CANDY,   ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_COURAGE_CANDY, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.SUPER_QUICK_CANDY,   ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.GOLD_BOTTLE_CAP,     ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModItems.SHINY_CHARM,   ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.EXP_CHARM,     ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.CATCH_CHARM,   ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.MULTI_CHARM,   ModelTemplates.FLAT_ITEM);

        for (CharmType type : CharmType.getEntries()) {
            var charm = ModItems.TYPE_CHARMS.get(type);
            if (charm != null) {
                generators.generateFlatItem(charm, ModelTemplates.FLAT_ITEM);
            }
        }
    }
}