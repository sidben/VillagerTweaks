package sidben.villagertweaks;


import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;



public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player) {
        // Debug
        /*
         * ModVillagerTweaks.logDebugInfo("PacketHandler.onPacketData");
         * ModVillagerTweaks.logDebugInfo("    Channel: " + payload.channel);
         * ModVillagerTweaks.logDebugInfo("    Side:    " +
         * FMLCommonHandler.instance().getEffectiveSide()); if (player != null)
         * { ModVillagerTweaks.logDebugInfo("    Player:  " +
         * player.toString()); }
         */

    }

}
