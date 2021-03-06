/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.ChromatiCraft.Auxiliary.ChromaRenderList;
import Reika.ChromatiCraft.Auxiliary.DonatorPylonRender;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockChromaPortal.TileEntityCrystalPortal;
import Reika.ChromatiCraft.Block.Crystal.BlockColoredAltar.TileEntityColoredAltar;
import Reika.ChromatiCraft.Block.Dimension.BlockVoidRift.TileEntityVoidRift;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector.TargetTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Entity.EntityAbilityFireball;
import Reika.ChromatiCraft.Entity.EntityAurora;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.ChromatiCraft.Entity.EntityGluon;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Entity.EntityMeteorShot;
import Reika.ChromatiCraft.Entity.EntitySplashGunShot;
import Reika.ChromatiCraft.Entity.EntityThrownGem;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.ChromatiCraft.Models.ColorizableSlimeModel;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.AltarItemRenderer;
import Reika.ChromatiCraft.Render.ChromaItemRenderer;
import Reika.ChromatiCraft.Render.EnderCrystalRenderer;
import Reika.ChromatiCraft.Render.LootChestRenderer;
import Reika.ChromatiCraft.Render.PortalItemRenderer;
import Reika.ChromatiCraft.Render.Entity.RenderAurora;
import Reika.ChromatiCraft.Render.Entity.RenderBallLightning;
import Reika.ChromatiCraft.Render.Entity.RenderChainGunShot;
import Reika.ChromatiCraft.Render.Entity.RenderDimensionFlare;
import Reika.ChromatiCraft.Render.Entity.RenderGluon;
import Reika.ChromatiCraft.Render.Entity.RenderLaserPulse;
import Reika.ChromatiCraft.Render.Entity.RenderMeteorShot;
import Reika.ChromatiCraft.Render.Entity.RenderSplashGunShot;
import Reika.ChromatiCraft.Render.Entity.RenderThrownGem;
import Reika.ChromatiCraft.Render.Entity.RenderVacuum;
import Reika.ChromatiCraft.Render.ISBRH.ColorLockRenderer;
import Reika.ChromatiCraft.Render.ISBRH.ConsoleRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalFenceRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalGlassRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalGlowRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystallineStoneRenderer;
import Reika.ChromatiCraft.Render.ISBRH.DecoFlowerRenderer;
import Reika.ChromatiCraft.Render.ISBRH.DecoPlantRenderer;
import Reika.ChromatiCraft.Render.ISBRH.DimensionDecoRenderer;
import Reika.ChromatiCraft.Render.ISBRH.GlowTreeRenderer;
import Reika.ChromatiCraft.Render.ISBRH.LampRenderer;
import Reika.ChromatiCraft.Render.ISBRH.LaserEffectorRenderer;
import Reika.ChromatiCraft.Render.ISBRH.PowerTreeRenderer;
import Reika.ChromatiCraft.Render.ISBRH.RelayRenderer;
import Reika.ChromatiCraft.Render.ISBRH.RuneRenderer;
import Reika.ChromatiCraft.Render.ISBRH.SelectiveGlassRenderer;
import Reika.ChromatiCraft.Render.ISBRH.SpecialShieldRenderer;
import Reika.ChromatiCraft.Render.ISBRH.TankBlockRenderer;
import Reika.ChromatiCraft.Render.ISBRH.TieredOreRenderer;
import Reika.ChromatiCraft.Render.ISBRH.TieredPlantRenderer;
import Reika.ChromatiCraft.Render.ISBRH.VoidRiftRenderer;
import Reika.ChromatiCraft.Render.TESR.CrystalPlantRenderer;
import Reika.ChromatiCraft.Render.TESR.RenderColoredAltar;
import Reika.ChromatiCraft.Render.TESR.RenderCrystalPortal;
import Reika.ChromatiCraft.Render.TESR.RenderLaserTarget;
import Reika.ChromatiCraft.Render.TESR.RenderLootChest;
import Reika.ChromatiCraft.Render.TESR.RenderVoidRift;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController.Donator;
import Reika.DragonAPI.Auxiliary.Trackers.KeybindHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PatreonController;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerSpecificRenderer;
import Reika.DragonAPI.Instantiable.IO.SoundLoader;
import Reika.DragonAPI.Instantiable.Rendering.ForcedTextureArmorModel;
import Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer;
import Reika.DragonAPI.Instantiable.Rendering.MultiSheetItemRenderer;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ChromaClient extends ChromaCommon {

	public static final ItemSpriteSheetRenderer items = new MultiSheetItemRenderer(ChromatiCraft.instance, ChromatiCraft.class);

	//public static final ItemMachineRenderer machineItems = new ItemMachineRenderer();

	private static final HashMap<ChromaItems, ForcedTextureArmorModel> armorTextures = new HashMap();
	private static final HashMap<ChromaItems, String> armorAssets = new HashMap();

	private static final ChromaItemRenderer placer = new ChromaItemRenderer();

	private static final CrystalRenderer crystal = new CrystalRenderer();
	private static final RuneRenderer rune = new RuneRenderer();
	private static final CrystallineStoneRenderer crystalstone = new CrystallineStoneRenderer();
	private static final TankBlockRenderer tank = new TankBlockRenderer();
	private static final PowerTreeRenderer tree = new PowerTreeRenderer();
	private static final LampRenderer lamp = new LampRenderer();
	private static final RelayRenderer relay = new RelayRenderer();
	private static final CrystalGlowRenderer glow = new CrystalGlowRenderer();
	private static final VoidRiftRenderer vrift = new VoidRiftRenderer();
	private static final DimensionDecoRenderer dimgen = new DimensionDecoRenderer();
	private static final GlowTreeRenderer glowtree = new GlowTreeRenderer();
	private static final ColorLockRenderer colorlock = new ColorLockRenderer();
	private SpecialShieldRenderer specialshield;
	private static final CrystalGlassRenderer glass = new CrystalGlassRenderer();
	private static final ConsoleRenderer console = new ConsoleRenderer();
	private static final CrystalFenceRenderer fence = new CrystalFenceRenderer();
	private static final SelectiveGlassRenderer selective = new SelectiveGlassRenderer();
	private static final LaserEffectorRenderer lasereffect = new LaserEffectorRenderer();

	//private static FiberRenderer fiber;

	private static final TieredOreRenderer ore = new TieredOreRenderer();
	public static final TieredPlantRenderer plant = new TieredPlantRenderer();

	public static final DecoPlantRenderer plant2 = new DecoPlantRenderer();

	public static final DecoFlowerRenderer flower = new DecoFlowerRenderer();

	private static final EnderCrystalRenderer csr = new EnderCrystalRenderer();

	public static KeyBinding key_ability;

	public static SoundCategory chromaCategory;

	@Override
	public void registerSounds() {
		new SoundLoader(ChromaSounds.soundList).register();

		chromaCategory = ReikaRegistryHelper.addSoundCategory("CHROMA", "ChromatiCraft");
	}

	@Override
	public void registerRenderers() {
		if (DragonOptions.NORENDERS.getState()) {
			ChromatiCraft.logger.log("Disabling all machine renders for FPS and lag profiling.");
		}
		else {
			this.loadModels();
		}

		RenderingRegistry.registerEntityRenderingHandler(EntityBallLightning.class, new RenderBallLightning());
		RenderingRegistry.registerEntityRenderingHandler(EntityAbilityFireball.class, new RenderFireball(2));
		RenderingRegistry.registerEntityRenderingHandler(EntityGluon.class, new RenderGluon());
		RenderingRegistry.registerEntityRenderingHandler(EntityChainGunShot.class, new RenderChainGunShot());
		RenderingRegistry.registerEntityRenderingHandler(EntitySplashGunShot.class, new RenderSplashGunShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityVacuum.class, new RenderVacuum());
		RenderingRegistry.registerEntityRenderingHandler(EntityChromaEnderCrystal.class, new RenderEnderCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityMeteorShot.class, new RenderMeteorShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityAurora.class, new RenderAurora());
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownGem.class, new RenderThrownGem());
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserPulse.class, new RenderLaserPulse());
		RenderingRegistry.registerEntityRenderingHandler(EntityDimensionFlare.class, new RenderDimensionFlare());

		this.registerSpriteSheets();
		this.registerBlockSheets();

		RenderSlime slimeRenderer = (RenderSlime)RenderManager.instance.entityRenderMap.get(EntitySlime.class);
		slimeRenderer.scaleAmount = new ColorizableSlimeModel(0);
		ChromatiCraft.logger.log("Overriding Slime Renderer Edge Model.");
	}

	@Override
	public void registerKeys() {
		if (ChromaOptions.KEYBINDABILITY.getState()) {
			key_ability = new KeyBinding("Use Ability", -98, "ChromatiCraft"); //Middle mouse
			//ClientRegistry.registerKeyBinding(key_ability);
			KeybindHandler.instance.addKeybind(key_ability);
		}
	}

	@Override
	public void addArmorRenders() {
		/*
		ReikaTextureHelper.forceArmorTexturePath("/Reika/ChromatiCraft/Textures/Misc/bedrock_1.png");
		ReikaTextureHelper.forceArmorTexturePath("/Reika/ChromatiCraft/Textures/Misc/bedrock_2.png");
		ReikaTextureHelper.forceArmorTexturePath("/Reika/ChromatiCraft/Textures/Misc/steel_1.png");
		ReikaTextureHelper.forceArmorTexturePath("/Reika/ChromatiCraft/Textures/Misc/steel_2.png");
		ReikaTextureHelper.forceArmorTexturePath("/Reika/ChromatiCraft/Textures/Misc/IOGoggles.png");
		ReikaTextureHelper.forceArmorTexturePath("/Reika/ChromatiCraft/Textures/Misc/NVGoggles.png");
		ReikaTextureHelper.forceArmorTexturePath("/Reika/ChromatiCraft/Textures/Misc/NVHelmet.png");*/

		//addArmorTexture(ChromatiItems.JETPACK, "/Reika/ChromatiCraft/Textures/Misc/jet.png");
	}

	private static void addArmorTexture(ChromaItems item, String tex) {
		ChromatiCraft.logger.log("Adding armor texture for "+item+": "+tex);
		armorTextures.put(item, new ForcedTextureArmorModel(ChromatiCraft.class, tex, item.getArmorType()));
		String[] s = tex.split("/");
		String file = s[s.length-1];
		String defaultTex = "Chromaticraft:textures/models/armor/"+file;
		//ReikaJavaLibrary.pConsole(defaultTex);
		armorAssets.put(item, defaultTex);
	}

	public static ForcedTextureArmorModel getArmorRenderer(ChromaItems item) {
		return armorTextures.get(item);
	}

	public static String getArmorTextureAsset(ChromaItems item) {
		return armorAssets.get(item);
	}

	public void loadModels() {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles m = ChromaTiles.TEList[i];
			if (m.hasRender()) {
				ChromaRenderBase render = (ChromaRenderBase)ChromaRenderList.instantiateRenderer(m);
				//int[] renderLists = render.createLists();
				//GLListData.addListData(m, renderLists);
				ClientRegistry.bindTileEntitySpecialRenderer(m.getTEClass(), render);

				if (m == ChromaTiles.ADJACENCY) {
					for (int k = 0; k < 16; k++) {
						if (AdjacencyUpgrades.upgrades[k].isImplemented()) {
							ClientRegistry.bindTileEntitySpecialRenderer(AdjacencyUpgrades.upgrades[k].getTileClass(), render);
						}
					}
				}
			}
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalPlant.class, new CrystalPlantRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootChest.class, new RenderLootChest());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalPortal.class, new RenderCrystalPortal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVoidRift.class, new RenderVoidRift());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColoredAltar.class, new RenderColoredAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TargetTile.class, new RenderLaserTarget());

		MinecraftForgeClient.registerItemRenderer(ChromaItems.PLACER.getItemInstance(), placer);
		MinecraftForgeClient.registerItemRenderer(ChromaItems.RIFT.getItemInstance(), placer);
		MinecraftForgeClient.registerItemRenderer(ChromaItems.ADJACENCY.getItemInstance(), placer);

		crystalRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(crystalRender, crystal);

		runeRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(runeRender, rune);

		crystalStoneRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(crystalStoneRender, crystalstone);

		tankRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(tankRender, tank);

		treeRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(treeRender, tree);

		lampRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(lampRender, lamp);

		relayRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(relayRender, relay);

		glowRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(glowRender, glow);

		vriftRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(vriftRender, vrift);

		dimgenRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(dimgenRender, dimgen);

		glowTreeRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(glowTreeRender, glowtree);

		colorLockRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(colorLockRender, colorlock);

		glassRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(glassRender, glass);

		consoleRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(consoleRender, console);

		fenceRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(fenceRender, fence);

		specialShieldRender = RenderingRegistry.getNextAvailableRenderId();
		specialshield = new SpecialShieldRenderer();
		RenderingRegistry.registerBlockHandler(specialShieldRender, specialshield);

		selectiveRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(selectiveRender, selective);

		lasereffectRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(lasereffectRender, lasereffect);

		//fiberRender = RenderingRegistry.getNextAvailableRenderId();
		//fiber = new FiberRenderer(fiberRender);
		//RenderingRegistry.registerBlockHandler(fiberRender, fiber);

		oreRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(oreRender, ore);
		plantRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(plantRender, plant);

		plantRender2 = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(plantRender2, plant2);

		flowerRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(flowerRender, flower);

		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuardianStone.class, new GuardianStoneRenderer());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalPlant.class, new CrystalPlantRenderer());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAccelerator.class, new AcceleratorRenderer());

		//MinecraftForgeClient.registerItemRenderer(ChromaBlocks.GUARDIAN.getItem(), teibr);
		//MinecraftForgeClient.registerItemRenderer(ChromaBlocks.ACCELERATOR.getItem(), teibr);

		MinecraftForgeClient.registerItemRenderer(ChromaItems.ENDERCRYSTAL.getItemInstance(), csr);

		MinecraftForgeClient.registerItemRenderer(ChromaBlocks.PORTAL.getItem(), new PortalItemRenderer());
		MinecraftForgeClient.registerItemRenderer(ChromaBlocks.COLORALTAR.getItem(), new AltarItemRenderer());
		MinecraftForgeClient.registerItemRenderer(ChromaBlocks.LOOTCHEST.getItem(), new LootChestRenderer());
	}


	private void registerBlockSheets() {
		//RenderingRegistry.registerBlockHandler(BlockSheetTexRenderID, block);
	}

	private void registerSpriteSheets() {
		for (int i = 0; i < ChromaItems.itemList.length; i++) {
			ChromaItems c = ChromaItems.itemList[i];
			if (!c.isPlacer() && c != ChromaItems.POTION)
				MinecraftForgeClient.registerItemRenderer(ChromaItems.itemList[i].getItemInstance(), items);
		}
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

	@Override
	public void addDonatorRender() {
		Collection<Donator> donators = new ArrayList();
		donators.addAll(DonatorController.instance.getReikasDonators());
		donators.addAll(PatreonController.instance.getModPatrons(DragonAPIInit.instance));
		for (Donator s : donators) {
			if (s.ingameName != null)
				PlayerSpecificRenderer.instance.registerRenderer(s.ingameName, DonatorPylonRender.instance);
			else
				ChromatiCraft.logger.logError("Donator "+s.displayName+" UUID could not be found! Cannot give special render!");
		}
	}

	@Override
	public void logPopupWarning(String msg) {
		PopupWriter.instance.addMessage(msg);
	}

}
