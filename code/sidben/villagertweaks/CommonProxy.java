package sidben.villagertweaks;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;



public class CommonProxy implements IGuiHandler {

    /*-------------------------------------------------------------------
        Server Logic
    -------------------------------------------------------------------*/

    // returns an instance of the Container
    @Override
    public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }


    /*-------------------------------------------------------------------
        Client Logic
    -------------------------------------------------------------------*/
    public void registerRenderers() {}


    // returns an instance of the GUI
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }


    public World getClientWorld() {
        return null;
    }

}