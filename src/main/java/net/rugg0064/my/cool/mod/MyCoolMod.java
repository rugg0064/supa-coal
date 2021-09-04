package net.rugg0064.my.cool.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class MyCoolMod implements ModInitializer {
	
	public static final SuperCoal SUPER_COAL = new SuperCoal(new Item.Settings().group(ItemGroup.MISC));
	public static final SuperCoalBlock SUPER_COAL_BLOCK = new SuperCoalBlock();
	public static final SuperCoalFurnace SUPER_COAL_FURNACE = new SuperCoalFurnace();
	public static final SuperCoalOre SUPER_COAL_ORE = new SuperCoalOre();
	// Create ore feature config for the ORE.
	public static ConfiguredFeature<?, ?> SUPER_COAL_OVERWORLD = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					SUPER_COAL_ORE.getDefaultState(),
				6
			))
			.range(new RangeDecoratorConfig(
					UniformHeightProvider.create(YOffset.aboveBottom(0), YOffset.fixed(64))
			))
			.spreadHorizontally()
			.repeat(30);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registry.BLOCK, new Identifier("coolmod", "super_coal_ore"), SUPER_COAL_ORE);
		Registry.register(Registry.ITEM, new Identifier("coolmod", "super_coal_ore"), new BlockItem(SUPER_COAL_ORE, new Item.Settings().group(ItemGroup.MISC)));

		//Registers the generation on initilization
			RegistryKey<ConfiguredFeature<?, ?>> superCoalOverworld = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
					new Identifier("coolmod", "super_cool_overworld"));
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, superCoalOverworld.getValue(), SUPER_COAL_OVERWORLD);
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, superCoalOverworld);

		//Register the super coal block item
		Registry.register(Registry.ITEM, new Identifier("coolmod", "super_coal"), SUPER_COAL);
		//Register the super coal block item as a fuel
		FuelRegistry.INSTANCE.add(SUPER_COAL, 20000);

		//Register the super coal block
		Registry.register(Registry.BLOCK, new Identifier("coolmod", "super_coal_block"), SUPER_COAL_BLOCK);
		//Register the super coal block item
		Registry.register(Registry.ITEM, new Identifier("coolmod", "super_coal_block"), new BlockItem(SUPER_COAL_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
		//Register the super coal block item as a fuel
		FuelRegistry.INSTANCE.add(SUPER_COAL_BLOCK, 20000 * 9);

		//Register the super coal furnace block
		Registry.register(Registry.BLOCK, new Identifier("coolmod", "super_coal_furnace"), SUPER_COAL_FURNACE);
		//Register the super coal furnace item
		Registry.register(Registry.ITEM, new Identifier("coolmod", "super_coal_furnace"), new BlockItem(SUPER_COAL_FURNACE, new Item.Settings().group(ItemGroup.MISC)));

	}
}
