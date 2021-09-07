package net.rugg0064.my.cool.mod;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;

public class OvenBricks extends BlockWithEntity
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

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new OvenBrickEntity(pos, state);
    }

    public BlockRenderType getRenderType(BlockState state)
    {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world,
                         BlockPos pos,
                         BlockState state,
                         LivingEntity placer,
                         ItemStack itemStack)
    {

        updateMultiBlocks(world, pos, true);
    }

    public static void updateMultiBlocks(World world, BlockPos pos, boolean placed)
    {
        if(placed)
        {
            //Run floodfill on current block
            updateMultiBlocksHelper(world, pos);
        }
        else
        {
            updateMultiBlocksHelper(world, pos.add(0,0,-1));
            updateMultiBlocksHelper(world, pos.add(0,0,1));
            updateMultiBlocksHelper(world, pos.add(0,-1,0));
            updateMultiBlocksHelper(world, pos.add(0,1,0));
            updateMultiBlocksHelper(world, pos.add(-1,0,0));
            updateMultiBlocksHelper(world, pos.add(1,0,0));
            //Run floodfill on all adjacent blocks
        }
    }

    public static void updateMultiBlocksHelper(World world, BlockPos pos)
    {
        BlockPos[] blocks = ovenBrickFloodFill(world, pos);
        if(blocks.length >= 1)
        {
            //Get the most min possible position
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;

            for(int i = 0; i < blocks.length; i++)
            {
                //Get the position
                BlockPos curPos = blocks[i];
                //Set minX/Y/Z to the minimums so far (each axis independent)
                int curX = curPos.getX();
                if(minX > curX) { minX = curX; }
                int curY = curPos.getY();
                if(minY > curY) { minY = curY; }
                int curZ = curPos.getZ();
                if(minZ > curZ) { minZ = curZ; }

                //Get the entity
                //OvenBrickEntity blockEntity = (OvenBrickEntity) world.getBlockEntity(curPos);
                //Assume its invalid
                //blockEntity.setValidConfiguration(false);
            }

            //if brick at min pos is an oven brick, then set all bricks' parents to the minpos brick
            BlockEntity minPosEntity = world.getBlockEntity(new BlockPos(minX,minY,minZ));
            if(minPosEntity instanceof OvenBrickEntity)
            {
                for(int i = 0; i < blocks.length; i++)
                {
                    ((OvenBrickEntity) world.getBlockEntity(blocks[i])).setParentPos(minPosEntity.getPos());
                }
            }

            int intCubeRoot = (int) Math.ceil(Math.cbrt(blocks.length));
            boolean onlyBrickEntities = true;
            for(int x = minX; x <= minX + intCubeRoot-1 && onlyBrickEntities; x++)
            {
                for(int y = minY; y <= minY + intCubeRoot-1 && onlyBrickEntities; y++)
                {
                    for(int z = minZ; z <= minZ + intCubeRoot-1 && onlyBrickEntities; z++)
                    {
                        boolean result = world.getBlockEntity(new BlockPos(x,y,z)) instanceof OvenBrickEntity;
                        //System.out.printf("Checking %d %d %d: %b%n", x, y, z, result);
                        onlyBrickEntities = result;
                    }
                }
            }
            //System.out.printf("Finished Checking, onlyBricks? %b%n", onlyBrickEntities);

            if(onlyBrickEntities)
            {
                System.out.println("Is a perfect cube!");
            }
            else
            {
                System.out.println("Not a perfect cube :-(");
            }

            for(int i = 0; i < blocks.length; i++)
            {
                BlockPos curBlockPos = blocks[i];
                OvenBrickEntity curOvenBrick = (OvenBrickEntity) world.getBlockEntity(curBlockPos);
                if(!onlyBrickEntities)
                {
                    System.out.println("Trying to spew contents");
                    curOvenBrick.spewContents();
                }
                curOvenBrick.setValidConfiguration(onlyBrickEntities);
                System.out.println("Setting the size to " + intCubeRoot);
                curOvenBrick.setSize(intCubeRoot);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit)
    {
        if (!world.isClient)
        {
            OvenBrickEntity blockUsed = (OvenBrickEntity) world.getBlockEntity(pos);

            if(blockUsed.isValidConfiguration())
            {
                BlockPos parentPos = blockUsed.getParentPos();

                OvenBrickEntity parent = (OvenBrickEntity)world.getBlockEntity(parentPos);

                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, parentPos);

                if(screenHandlerFactory != null)
                {
                    player.openHandledScreen(screenHandlerFactory);
                }

            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {

        if (state.getBlock() != newState.getBlock()) {
            //Broke block
            super.onStateReplaced(state, world, pos, newState, moved);
            updateMultiBlocks(world, pos, false);
        }
    }

    public static BlockPos[] ovenBrickFloodFill(World world, BlockPos pos)
    {
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        ovenBrickFloodFillHelper(world, pos, visited, blocks);
        BlockPos[] arr = new BlockPos[blocks.size()];
        blocks.toArray(arr);
        return arr;
    }

    public static void ovenBrickFloodFillHelper(World world, BlockPos pos, HashSet<BlockPos> visited, ArrayList<BlockPos> blocks)
    {
        if(!visited.contains(pos))
        {
            visited.add(pos);
            if(world.getBlockEntity(pos) instanceof OvenBrickEntity)
            {
                blocks.add(pos);
                ovenBrickFloodFillHelper(world, pos.add(0,0,-1), visited, blocks);
                ovenBrickFloodFillHelper(world, pos.add(0,0,1), visited, blocks);
                ovenBrickFloodFillHelper(world, pos.add(0,-1,0), visited, blocks);
                ovenBrickFloodFillHelper(world, pos.add(0,1,0), visited, blocks);
                ovenBrickFloodFillHelper(world, pos.add(-1,0,0), visited, blocks);
                ovenBrickFloodFillHelper(world, pos.add(1,0,0), visited, blocks);
            }
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return checkType(type, MyCoolMod.OVEN_BRICK_ENTITY, (world1, pos, state1, be) -> OvenBrickEntity.tick(world1, pos, state1, be));
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}
