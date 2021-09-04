package net.rugg0064.my.cool.mod;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class TestBlock extends Block
{
	public TestBlock()
	{
		super(
				FabricBlockSettings
						.of(Material.WOOD)
						.breakByHand(false)
						.breakByTool(FabricToolTags.AXES)
						.hardness(2.0f)
						.resistance(0.5f)
				);
	}
	
	public TestBlock(Settings settings) {
		super(settings);
	}
}