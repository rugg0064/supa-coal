package net.rugg0064.my.cool.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MyCoolMod implements ModInitializer {
	
	public static final SuperCoal SUPER_COAL = new SuperCoal(new Item.Settings().group(ItemGroup.MISC));
	public static final TestBlock FABRIC_BLOCK = new TestBlock();
	
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registry.ITEM, new Identifier("coolmod", "testitem"), SUPER_COAL);
		Registry.register(Registry.BLOCK, new Identifier("coolmod", "testblock"), FABRIC_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("coolmod", "testblock"), new BlockItem(FABRIC_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
		FuelRegistry.INSTANCE.add(SUPER_COAL, 36000);
	}
}
