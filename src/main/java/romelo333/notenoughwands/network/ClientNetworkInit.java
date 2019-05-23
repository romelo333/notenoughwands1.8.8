package romelo333.notenoughwands.network;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class ClientNetworkInit implements ClientModInitializer {

	public void onInitializeClient() {
		System.out.println("############ Set up client-side networking #############");

		ClientSidePacketRegistry.INSTANCE.register(PacketReturnProtectedBlocks.RETURN_PROTECTED_BLOCKS, new PacketReturnProtectedBlocks.Handler());
		ClientSidePacketRegistry.INSTANCE.register(PacketReturnProtectedBlockCount.RETURN_PROTECTED_BLOCK_COUNT, new PacketReturnProtectedBlockCount.Handler());
	}
}
