/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public final class RuneShape {

	/** Coords are relative to the BlockArray origin! */
	private final HashMap<Coordinate, CrystalElement> runes = new HashMap();
	private final BlockArray blocks = new BlockArray();

	public RuneShape() {

	}

	public RuneShape(Map<Coordinate, CrystalElement> map) {
		for (Coordinate c : map.keySet()) {
			this.addRune(map.get(c), c.xCoord, c.yCoord, c.zCoord);
		}
	}

	public void addRune(CrystalElement e, int x, int y, int z) {
		runes.put(new Coordinate(x, y, z), e);
		blocks.addBlockCoordinate(x, y, z);
	}

	public CrystalElement getRuneAt(int x, int y, int z) {
		return runes.get(new Coordinate(x, y, z));
	}

	public void place(World world, int x, int y, int z) {
		for (Coordinate c : runes.keySet()) {
			world.setBlock(c.xCoord+x, c.yCoord+y, c.zCoord+z, ChromaBlocks.RUNE.getBlockInstance(), runes.get(c).ordinal(), 3);
		}
	}

	public boolean matchAt(World world, int x, int y, int z, int xref, int yref, int zref) {
		for (Coordinate c : runes.keySet()) {
			int dx = x+c.xCoord-xref;
			int dy = y+c.yCoord-yref;
			int dz = z+c.zCoord-zref;
			Block b = world.getBlock(dx, dy, dz);
			if (b == ChromaBlocks.RUNE.getBlockInstance()) {
				int meta = world.getBlockMetadata(dx, dy, dz);
				if (meta == runes.get(c).ordinal()) {

				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		return true;
	}

	public int getMinX() {
		return blocks.getMinX();
	}

	public int getMaxX() {
		return blocks.getMaxX();
	}

	public int getMinY() {
		return blocks.getMinY();
	}

	public int getMaxY() {
		return blocks.getMaxY();
	}

	public int getMinZ() {
		return blocks.getMinZ();
	}

	public int getMaxZ() {
		return blocks.getMaxZ();
	}

	public int getSizeX() {
		return blocks.getSizeX();
	}

	public int getSizeY() {
		return blocks.getSizeY();
	}

	public int getSizeZ() {
		return blocks.getSizeZ();
	}

	@Override
	public String toString() {
		return runes.toString();
	}

	public boolean isEmpty() {
		return runes.isEmpty();
	}

	public RuneViewer getView() {
		return new RuneViewer(this);
	}

	public static class RuneLocation {

		public final CrystalElement color;
		public final int xCoord;
		public final int yCoord;
		public final int zCoord;
		public final int dimensionID;

		public RuneLocation(CrystalElement e, World world, int x, int y, int z) {
			color = e;
			xCoord = x;
			yCoord = y;
			zCoord = z;
			dimensionID = world.provider.dimensionId;
		}

		public RuneLocation(int meta, World world, int x, int y, int z) {
			this(CrystalElement.elements[meta], world, x, y, z);
		}

	}

	public static final class RuneViewer {

		private final RuneShape shape;

		private RuneViewer(RuneShape r) {
			shape = r;
		}

		public Map<Coordinate, CrystalElement> getRunes() {
			return Collections.unmodifiableMap(shape.runes);
		}

		public int getSizeX() {
			return shape.getSizeX();
		}

		public int getSizeY() {
			return shape.getSizeY();
		}

		public int getSizeZ() {
			return shape.getSizeZ();
		}

		public int getMinX() {
			return shape.getMinX();
		}

		public int getMinY() {
			return shape.getMinY();
		}

		public int getMinZ() {
			return shape.getMinZ();
		}

		public int getMaxX() {
			return shape.getMaxX();
		}

		public int getMaxY() {
			return shape.getMaxY();
		}

		public int getMaxZ() {
			return shape.getMaxZ();
		}

		public boolean isEmpty() {
			return shape.isEmpty();
		}
	}
}
