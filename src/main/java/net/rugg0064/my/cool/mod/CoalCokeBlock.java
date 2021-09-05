package net.rugg0064.my.cool.mod;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class CoalCokeBlock extends Block
{
	public CoalCokeBlock()
	{
		super(
				FabricBlockSettings
						.of(Material.STONE)
						.breakByHand(false)
						.breakByTool(FabricToolTags.PICKAXES, 3)
						.hardness(4.5f)
						.resistance(6f)
				);
	}
}
