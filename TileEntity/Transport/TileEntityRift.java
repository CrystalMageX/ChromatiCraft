/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.IManaCollisionGhost;
import vazkii.botania.api.mana.IManaReceiver;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.ASM.InterfaceInjector.Injectable;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Base.BlockTEBase;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Strippable(value = {"cofh.api.energy.IEnergyHandler", "thaumcraft.api.aspects.IEssentiaTransport",
		"thaumcraft.api.aspects.IAspectContainer", "vazkii.botania.api.mana.IManaCollisionGhost", "vazkii.botania.api.mana.IManaReceiver"})
@Injectable(value = {"dan200.computercraft.api.peripheral.IPeripheral", "li.cil.oc.api.network.Environment",
		"li.cil.oc.api.network.ManagedPeripheral", "li.cil.oc.api.network.SidedEnvironment"})
public class TileEntityRift extends TileEntityChromaticBase implements WorldRift, SneakPop, IFluidHandler, IEnergyHandler,
IEssentiaTransport, IAspectContainer, ISidedInventory, ChunkLoadingTile, IManaCollisionGhost, IManaReceiver {

	private WorldLocation target;
	private int color = 0xffffff;
	private int[] redstoneCache = new int[6];
	private ForgeDirection singleDirection;
	private boolean shouldDrop;

	private final Object[] sidedOCNode = new Object[6];

	public TileEntityRift() {
		if (ModList.OPENCOMPUTERS.isLoaded() && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			for (int i = 0; i < 6; i++) {
				sidedOCNode[i] = Network.newNode((Environment)this, Visibility.Network).create();
			}
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.RIFT;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (shouldDrop) {
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, ChromaItems.RIFT.getStackOf());
			this.delete();
		}
		if (!world.isRemote && this.isLinked() && this.getOther() != null) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);//.expand(0.15, 0.15, 0.15);
			List<Entity> li = world.getEntitiesWithinAABB(Entity.class, box);
			for (Entity e : li) {
				if (this.shouldTeleport(e)) {
					this.teleport(e);
				}
			}
		}
	}

	private void teleport(Entity e) {
		double rx = 1-(e.posX-xCoord);
		double ry = 1-(e.posY-yCoord);
		double rz = 1-(e.posZ-zCoord);
		TileEntityRift te = this.getOther();
		if (ModList.BOTANIA.isLoaded() && e instanceof IManaBurst) {
			IManaBurst b = (IManaBurst)e;
			/*
			int c = b.getColor();
			float[] hsb = Color.RGBtoHSB(ReikaColorAPI.getRed(c), ReikaColorAPI.getGreen(c), ReikaColorAPI.getBlue(c), null);
			hsb[1] = 1;
			hsb[0] += 0.1667F; //60 deg purpleshift
			if (hsb[0] > 1)
				hsb[0] -= 1;
			c = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			b.setColor(c);
			 */
			b.setColor(color);
			b.setBurstSourceCoords(te.xCoord, te.yCoord, te.zCoord);
			b.setMana(b.getStartingMana()); //reset life
			ReikaSoundHelper.playSoundAtBlock(worldObj, te.xCoord, te.yCoord, te.zCoord, "botania:spreaderFire", 0.5F, (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.25));
		}
		e.setLocationAndAngles(te.xCoord+rx, te.yCoord+ry, te.zCoord+rz, e.rotationYaw, e.rotationPitch);
		e.getEntityData().setLong("rift_teleported", worldObj.getTotalWorldTime());
	}

	private boolean shouldTeleport(Entity e) {
		long get = e.getEntityData().getLong("rift_teleported");
		if (worldObj.getTotalWorldTime()-get < 5)
			return false;
		if (ModList.BOTANIA.isLoaded() && e instanceof IManaBurst) {
			return true;
		}
		return false;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (ChromaOptions.RIFTLOAD.getState()) {
			ChunkManager.instance.loadChunks(this);
		}
	}

	public void setDirection(ForgeDirection dir) {
		if (this.isLinked() && this.getOther() != null) {
			singleDirection = dir;
			this.getOther().singleDirection = dir.getOpposite();
		}
	}

	public ForgeDirection getSingleDirection() {
		return singleDirection;
	}

	public void drop() {
		shouldDrop = true;
		TileEntityRift te = this.getOther();
		if (te != null)
			te.shouldDrop = true;
		this.reset();
	}

	public boolean canDrop(EntityPlayer ep) {
		return ep.getUniqueID().equals(placerUUID);
	}

	public void passThrough() {
		if (this.isLinked()) {
			TileEntityRift te = this.getOther();
			if (te != null) {
				for (int i = 0; i < 6; i++) {
					ForgeDirection dir = dirs[i];
					ForgeDirection opp = dir.getOpposite();
					int dx = target.xCoord+dir.offsetX;
					int dy = target.yCoord+dir.offsetY;
					int dz = target.zCoord+dir.offsetZ;
					int ddx = xCoord-dir.offsetX;
					int ddy = yCoord-dir.offsetY;
					int ddz = zCoord-dir.offsetZ;
					Block id = worldObj.getBlock(dx, dy, dz);
					Block id2 = worldObj.getBlock(ddx, ddy, ddz);
					int pwr = worldObj.getIndirectPowerLevelTo(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ, opp.ordinal());
					te.redstoneCache[i] = pwr;
					if (id != Blocks.air) {
						id.onNeighborBlockChange(worldObj, dx, dy, dz, id);
					}
					TileEntity tile = this.getAdjacentTileEntity(dir);
					if (tile != this && tile instanceof TileEntityBase) {
						((TileEntityBase)tile).updateCache(dir.getOpposite());
					}
				}
			}
			target.triggerBlockUpdate(true);
		}
	}

	public int getRedstoneLevel(ForgeDirection side) {
		return Math.max(redstoneCache[side.ordinal()]-1, 0);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isLinked() {
		return target != null;
	}

	public boolean linkTo(World world, int x, int y, int z) {
		if (!world.isRemote && this.canLinkTo(world, x, y, z)) {
			this.resetOther();
			target = new WorldLocation(world, x, y, z);
			color = this.getRandomColor();
			TileEntityRift te = this.getOther();
			te.target = new WorldLocation(worldObj, xCoord, yCoord, zCoord);
			te.color = color;
			this.onLink(true);
			if (ModList.OPENCOMPUTERS.isLoaded()) {
				for (int i = 0; i < 6; i++) {
					((Node)te.sidedOCNode[dirs[i].getOpposite().ordinal()]).connect((Node)sidedOCNode[i]);
				}
			}
			return true;
		}
		return false;
	}

	private void onLink(boolean other) {
		ChromaSounds.RIFT.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = xCoord+dir.offsetX;
			int dy = yCoord+dir.offsetY;
			int dz = zCoord+dir.offsetZ;
			Block b = worldObj.getBlock(dx, dy, dz);
			if (b instanceof BlockTEBase) {
				((BlockTEBase)b).updateTileCache(worldObj, dx, dy, dz);
			}
		}
		if (other && this.isLinked()) {
			this.getOther().onLink(false);
		}
	}

	public boolean linkTo(WorldLocation loc) {
		return !loc.equals(worldObj, xCoord, yCoord, zCoord) && this.linkTo(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public void reset() {
		this.resetOther();
		target = null;
		color = 0xffffff;
		redstoneCache = new int[6];
		singleDirection = null;
		if (ModList.OPENCOMPUTERS.isLoaded()) {
			for (int i = 0; i < 6; i++) {
				if (sidedOCNode[i] != null)
					((Node)sidedOCNode[i]).disconnect(((Node)this.getOther().sidedOCNode[i]));
			}
		}

		this.onLink(true);
	}

	public void resetOther() {
		if (this.isLinked()) {
			TileEntityRift te = this.getOther();
			if (te != null) {
				te.target = null;
				te.color = 0xffffff;
				if (ModList.OPENCOMPUTERS.isLoaded()) {
					for (int i = 0; i < 6; i++) {
						if (te.sidedOCNode[i] != null)
							((Node)te.sidedOCNode[i]).disconnect(((Node)sidedOCNode[i]));
					}
				}
				this.onLink(true);
			}
		}
	}

	public int getColor() {
		return this.isInWorld() ? color : Color.HSBtoRGB((System.currentTimeMillis()%15000)/15000F, 1, 1);
	}

	private int getRandomColor() {
		return Color.HSBtoRGB(rand.nextFloat(), 1, 1);
	}

	private boolean canLinkTo(World world, int x, int y, int z) {
		return new WorldLocation(world, x, y, z).getBlock() == ChromaBlocks.RIFT.getBlockInstance();
	}

	private TileEntityRift getOther() {
		return this.isLinked() ? ((TileEntityRift)target.getTileEntity()) : null;
	}

	public WorldLocation getBlockFrom(ForgeDirection from) {
		return this.isLinked() ? target.move(from.getOpposite(), 1) : null;
	}

	private TileEntity getAdjacentTargetTile(ForgeDirection dir) {
		return this.isLinked() && this.getOther() != null ? this.getOther().getAdjacentTileEntity(dir) : null;
	}

	private TileEntity getSingleDirTile() {
		return singleDirection != null && this.isLinked() ? this.getOther().getAdjacentTileEntity(singleDirection.getOpposite()) : null;
	}

	@Override
	public Block getBlockIDFrom(ForgeDirection dir) {
		return this.isLinked() ? this.getBlockFrom(dir).getBlock() : null;
	}

	@Override
	public int getBlockMetadataFrom(ForgeDirection dir) {
		return this.isLinked() ? this.getBlockFrom(dir).getBlockMetadata() : -1;
	}

	@Override
	public TileEntity getTileEntityFrom(ForgeDirection dir) {
		return this.isLinked() ? this.getAdjacentTargetTile(dir) : null;
	}

	@Override
	public WorldLocation getLinkTarget() {
		return target;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		if (this.isLinked())
			target.writeToNBT("target", NBT);
		NBT.setInteger("color", color);
		NBT.setIntArray("redstone", redstoneCache);
		NBT.setInteger("dir", singleDirection != null ? singleDirection.ordinal() : -1);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		if (NBT.hasKey("target"))
			target = WorldLocation.readFromNBT("target", NBT);
		color = NBT.getInteger("color");
		redstoneCache = NBT.getIntArray("redstone");
		int dir = NBT.getInteger("dir");
		singleDirection = dir != -1 ? dirs[dir] : null;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean isConnectable(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).isConnectable(face);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean canInputFrom(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).canInputFrom(face);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean canOutputTo(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).canOutputTo(face);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setSuction(Aspect aspect, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IEssentiaTransport) {
			((IEssentiaTransport)this.getSingleDirTile()).setSuction(aspect, amount);
		}
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getSuctionType(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).getSuctionType(face);
		}
		return null;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getSuctionAmount(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).getSuctionAmount(face);
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).takeEssentia(aspect, amount, face);
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).addEssentia(aspect, amount, face);
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getEssentiaType(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).getEssentiaType(face);
		}
		return null;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getEssentiaAmount(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).getEssentiaAmount(face);
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getMinimumSuction() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getSingleDirTile()).getMinimumSuction();
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean renderExtendedTube() {
		return false;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).extractEnergy(from, maxExtract, simulate);
		}
		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).canConnectEnergy(from);
		}
		return false;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).getEnergyStored(from);
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).getMaxEnergyStored(from);
		}
		return 0;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).fill(from, resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).drain(from, resource, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).drain(from, maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).canFill(from, fluid);
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).canDrain(from, fluid);
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).getTankInfo(from);
		}
		return null;
	}

	@Override
	public int getSizeInventory() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getStackInSlot(i);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).decrStackSize(i, j);
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getStackInSlotOnClosing(i);
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			((IInventory)this.getSingleDirTile()).setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getInventoryName() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getInventoryName();
		}
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).hasCustomInventoryName();
		}
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getInventoryStackLimit();
		}
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).isUseableByPlayer(entityplayer);
		}
		return false;
	}

	@Override
	public void openInventory() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			((IInventory)this.getSingleDirTile()).openInventory();
		}
	}

	@Override
	public void closeInventory() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			((IInventory)this.getSingleDirTile()).closeInventory();
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).isItemValidForSlot(i, itemstack);
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		if (this.getOther() != null) {
			if (this.getSingleDirTile() instanceof ISidedInventory) {
				return ((ISidedInventory)this.getSingleDirTile()).getAccessibleSlotsFromSide(var1);
			}
			else if (this.getSingleDirTile() instanceof IInventory) {
				int size = ((IInventory)this.getSingleDirTile()).getSizeInventory();
				int[] sides = new int[size];
				for (int i = 0; i < size; i++) {
					sides[i] = i;
				}
				return sides;
			}
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		if (this.getOther() != null) {
			if (this.getSingleDirTile() instanceof ISidedInventory) {
				return ((ISidedInventory)this.getSingleDirTile()).canInsertItem(i, itemstack, j);
			}
			else if (this.getSingleDirTile() instanceof IInventory) {
				return ((IInventory)this.getSingleDirTile()).isItemValidForSlot(i, itemstack);
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof ISidedInventory) {
			return ((ISidedInventory)this.getSingleDirTile()).canExtractItem(i, itemstack, j);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getAspects() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).getAspects();
		}
		return new AspectList();
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setAspects(AspectList aspects) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			((IAspectContainer)this.getSingleDirTile()).setAspects(aspects);
		}
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerAccept(Aspect tag) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).doesContainerAccept(tag);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addToContainer(Aspect tag, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).addToContainer(tag, amount);
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean takeFromContainer(Aspect tag, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).takeFromContainer(tag, amount);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean takeFromContainer(AspectList ot) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).takeFromContainer(ot);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).doesContainerContainAmount(tag, amount);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerContain(AspectList ot) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).doesContainerContain(ot);
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int containerContains(Aspect tag) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).containerContains(tag);
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public String getType() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			return ((IPeripheral)this.getSingleDirTile()).getType();
		}
		return "No connection";
	}

	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public String[] getMethodNames() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			return ((IPeripheral)this.getSingleDirTile()).getMethodNames();
		}
		return new String[0];
	}

	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			return ((IPeripheral)this.getSingleDirTile()).callMethod(computer, context, method, arguments);
		}
		return null;
	}

	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public void attach(IComputerAccess computer) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			((IPeripheral)this.getSingleDirTile()).attach(computer);
		}
	}

	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public void detach(IComputerAccess computer) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			((IPeripheral)this.getSingleDirTile()).detach(computer);
		}
	}
	/*
	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public String[] methods() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof ManagedPeripheral) {
			return ((ManagedPeripheral)this.getSingleDirTile()).methods();
		}
		return new String[0];
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		if (this.getOther() != null && this.getSingleDirTile() instanceof ManagedPeripheral) {
			return ((ManagedPeripheral)this.getSingleDirTile()).invoke(method, context, args);
		}
		return null;
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final Node node() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof Environment) {
			return ((Environment)this.getSingleDirTile()).node();
		}
		return null;
	}
	 */
	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final void onConnect(Node node) {
		TileEntityRift te = this.getOther();
		if (te != null) {
			for (int i = 0; i < 6; i++) {
				if (sidedOCNode[i] != null && sidedOCNode[i] == node && this.getOtherNode(te, i) != null) {
					((Node)sidedOCNode[i]).connect(this.getOtherNode(te, i));
				}
			}
		}
	}
	/*
	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final void onDisconnect(Node node) {
		TileEntityRift te = this.getOther();
		if (te != null) {
			for (int i = 0; i < 6; i++) {
				if (sidedOCNode[i] != null && sidedOCNode[i] == node && this.getOtherNode(te, i) != null) {
					((Node)sidedOCNode[i]).disconnect(this.getOtherNode(te, i));
				}
			}
		}
	}
	 */
	@ModDependent(ModList.OPENCOMPUTERS)
	private Node getOtherNode(TileEntityRift te, int side) {
		return (Node)te.sidedOCNode[dirs[side].getOpposite().ordinal()];
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		if (ModList.OPENCOMPUTERS.isLoaded()) {
			for (int i = 0; i < 6; i++) {
				if (sidedOCNode[i] != null) {
					((Node)sidedOCNode[i]).remove();
				}
			}
		}
	}
	/*
	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final void onMessage(Message message) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof Environment) {
			((Environment)this.getSingleDirTile()).onMessage(message);
		}
	}
	 */
	@Override
	public void breakBlock() {
		ChunkManager.instance.unloadChunks(this);
	}

	@Override
	public Collection<ChunkCoordIntPair> getChunksToLoad() {
		return ChunkManager.getChunkSquare(xCoord, zCoord, 2);
	}

	@Override
	public int getCurrentMana() {
		return 0;
	}

	@Override
	public boolean isGhost() {
		return false;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public void recieveMana(int mana) {

	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return true;
	}

	//@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public Node sidedNode(ForgeDirection side) { //OC
		return (Node)sidedOCNode[side.ordinal()];
	}

	//@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public boolean canConnect(ForgeDirection side) { //OC
		return this.isLinked();
	}

}
