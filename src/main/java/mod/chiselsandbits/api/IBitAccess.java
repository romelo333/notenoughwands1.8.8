package mod.chiselsandbits.api;

import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * Do not implement, acquire from {@link IChiselAndBitsAPI}
 */
public interface IBitAccess
{

	/**
	 * Returns the bit at the specific location, this should always return a
	 * valid IBitBrush, never null.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	IBitBrush getBitAt(
			int x,
			int y,
			int z );

	/**
	 * Sets the bit at the specific location, if you pass null it will use use
	 * air.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param bit
	 * @throws SpaceOccupied
	 */
	void setBitAt(
			int x,
			int y,
			int z,
			IBitBrush bit ) throws SpaceOccupied;

	/**
	 * Any time you modify a block you must commit your changes for them to take
	 * affect.
	 *
	 * If the {@link IBitAccess} is not in the world this method does nothing.
	 */
	void commitChanges();

	/**
	 * Returns an item for the {@link IBitAccess}
	 *
	 * Usable for any {@link IBitAccess}
	 *
	 * @param side
	 *            angle the player is looking at, can be null.
	 * @param type
	 *            what type of item to give.
	 * @return an Item for bits, null if there are no bits.
	 */
	ItemStack getBitsAsItem(
			EnumFacing side,
			ItemType type );

}
