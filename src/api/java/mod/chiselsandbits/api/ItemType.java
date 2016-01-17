package mod.chiselsandbits.api;

public enum ItemType
{
	CHISLED_BLOCK( true ),
	POSITIVE_DESIGN( true ),
	NEGATIVE_DESIGN( true ),
	MIRROR_DESIGN( true ),
	CHISEL( false ),
	BIT_BAG( false ),
	CHISLED_BIT( false ),
	WRENCH( false );

	public final boolean isBitAccess;

	private ItemType(
			final boolean bitAccess )
	{
		isBitAccess = bitAccess;
	}

}
