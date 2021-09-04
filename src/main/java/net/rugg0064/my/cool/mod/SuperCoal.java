package net.rugg0064.my.cool.mod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SuperCoal extends Item{

	public SuperCoal(Settings settings) {
		super(settings);
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand)
	{
		playerEntity.playSound(SoundEvents.BLOCK_LADDER_BREAK, 1.0f, 1.0f);
		playerEntity.dropStack(playerEntity.getStackInHand(hand));
		playerEntity.getInventory().removeOne(playerEntity.getStackInHand(hand));
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
	}

	
}
