package sidben.villagertweaks;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;



public class ClientProxy extends CommonProxy {

    /*-------------------------------------------------------------------
        Client Logic
    -------------------------------------------------------------------*/

    @Override
    public void registerRenderers() {}


    // returns an instance of the GUI
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }


    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

}