package net.rugg0064.my.cool.mod;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class SuperCoalOre extends Block
{
    public SuperCoalOre()
    {
        super(
                FabricBlockSettings
                        .of(Material.STONE)
                        .breakByHand(false)
                        .breakByTool(FabricToolTags.PICKAXES, 2)
                        .hardness(4.5f)
                        .resistance(6f)
        );
    }


}