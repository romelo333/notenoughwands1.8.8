package mod.chiselsandbits.api;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Do not implement, is passed to your {@link IChiselsAndBitsAddon}
 */
public interface IChiselAndBitsAPI
{

	/**
	 * Determine the Item Type and return it.
	 *
	 * @param item
	 * @return ItemType of the item, or null if it is not any of them.
	 */
	ItemType getItemType(
			ItemStack item );

	/**
	 * Check if a block can support {@link IBitAccess}
	 *
	 * @param world
	 * @param pos
	 * @return true if the block can be chiseled, this is true for air,
	 *         multi-parts, and blocks which can be chiseled, false otherwise.
	 */
	boolean canBeChiseled(
			World world,
			BlockPos pos );

	/**
	 * is this block already chiseled?
	 *
	 * @param world
	 * @param pos
	 * @return true if the block contains chiseled bits, false otherwise.
	 */
	boolean isBlockChiseled(
			World world,
			BlockPos pos );

	/**
	 * Get Access to the bits for a given block.
	 *
	 * @param world
	 * @param pos
	 * @return A {@link IBitAccess} for the specified location.
	 * @throws CannotBeChiseled
	 *             when the location cannot support bits, or if the parameters
	 *             are invalid.
	 */
	IBitAccess getBitAccess(
			World world,
			BlockPos pos ) throws CannotBeChiseled;

	/**
	 * Create a bit access from an item, passing null creates an empty item,
	 * passing an invalid item returns null.
	 *
	 * @return a {@link IBitAccess} for an item.
	 */
	IBitAccess createBitItem(
			ItemStack BitItemStack );

	/**
	 * Create a brush from an item, once created you can use it many times.
	 *
	 * @param bitItem
	 * @return A brush for the specified item, if null is passed for the item an
	 *         air brush is created.
	 * @throws InvalidBitItem
	 */
	IBitBrush createBrush(
			ItemStack bitItem ) throws InvalidBitItem;

	/**
	 * Convert ray trace information into bit location information, note that
	 * the block position can change, be aware.
	 *
	 * @param hitX
	 * @param hitY
	 * @param hitZ
	 * @param side
	 * @param pos
	 * @param placement
	 * @return details about the target bit, if any parameters are missing will
	 *         return null.
	 */
	IBitLocation getBitPos(
			float hitX,
			float hitY,
			float hitZ,
			EnumFacing side,
			BlockPos pos,
			boolean placement );

	/**
	 * Get an ItemStack for the bit type of the state...
	 * 
	 * VERY IMPORTANT: C&B lets you disable bits, if this happens the Item in
	 * this ItemStack WILL BE NULL, if you put this item in an inventory, drop
	 * it on the ground, or anything else.. CHECK THIS!!!!!
	 * 
	 * @param defaultState
	 * @return the bit.
	 */
	ItemStack getBitItem(
			IBlockState defaultState ) throws InvalidBitItem;

}
