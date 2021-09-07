package net.rugg0064.my.cool.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class MyCoolMod implements ModInitializer {
	
	public static final CoalCoke COAL_COKE = new CoalCoke(new Item.Settings().group(ItemGroup.MISC));
	public static final CoalCokeBlock COAL_COKE_BLOCK = new CoalCokeBlock();
	public static final OvenBricks OVEN_BRICKS = new OvenBricks();
	public static final CoalCokeOre COAL_COKE_ORE = new CoalCokeOre();

	public static ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER;

	public static BlockEntityType<OvenBrickEntity> OVEN_BRICK_ENTITY;
	// Create ore feature config for the ORE.
	public static ConfiguredFeature<?, ?> SUPER_COAL_OVERWORLD = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					COAL_COKE_ORE.getDefaultState(),
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

		BOX_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("coolmod", "box_screen_handler"), BoxScreenHandler::new);

		//Register the furnace block entity
		OVEN_BRICK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("coolmod", "oven_brick_entity"), FabricBlockEntityTypeBuilder.create(OvenBrickEntity::new, OVEN_BRICKS).build(null));

		//Register coal ore block
		Registry.register(Registry.BLOCK, new Identifier("coolmod", "coal_coke_ore"), COAL_COKE_ORE);
		//Register coal ore block item
		Registry.register(Registry.ITEM, new Identifier("coolmod", "coal_coke_ore"), new BlockItem(COAL_COKE_ORE, new Item.Settings().group(ItemGroup.MISC)));

		//Registers the generation on initilization
		RegistryKey<ConfiguredFeature<?, ?>> superCoalOverworld = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
				new Identifier("coolmod", "super_cool_overworld"));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, superCoalOverworld.getValue(), SUPER_COAL_OVERWORLD);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, superCoalOverworld);

		//Register the super coal block item
		Registry.register(Registry.ITEM, new Identifier("coolmod", "coal_coke"), COAL_COKE);
		//Register the super coal block item as a fuel
		FuelRegistry.INSTANCE.add(COAL_COKE, 20000);

		//Register the super coal block
		Registry.register(Registry.BLOCK, new Identifier("coolmod", "coal_coke_block"), COAL_COKE_BLOCK);
		//Register the super coal block item
		Registry.register(Registry.ITEM, new Identifier("coolmod", "coal_coke_block"), new BlockItem(COAL_COKE_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
		//Register the super coal block item as a fuel
		FuelRegistry.INSTANCE.add(COAL_COKE_BLOCK, 20000 * 9);

		//Register the super coal furnace block
		Registry.register(Registry.BLOCK, new Identifier("coolmod", "oven_bricks"), OVEN_BRICKS);
		//Register the super coal furnace item
		Registry.register(Registry.ITEM, new Identifier("coolmod", "oven_bricks"), new BlockItem(OVEN_BRICKS, new Item.Settings().group(ItemGroup.MISC)));


		ScreenRegistry.register(MyCoolMod.BOX_SCREEN_HANDLER, BoxScreen::new);
	}

	public void onInitializeClient()
	{
	}

}
