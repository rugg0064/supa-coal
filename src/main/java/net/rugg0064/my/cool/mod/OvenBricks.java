package net.rugg0064.my.cool.mod;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class OvenBricks extends Block
{
    public OvenBricks()
    {
        super(FabricBlockSettings
                .of(Material.STONE)
                .breakByHand(false)
                .breakByTool(FabricToolTags.PICKAXES, 2)
                .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
                .hardness(4.5f)
                .resistance(6f)
        );
    }

}
