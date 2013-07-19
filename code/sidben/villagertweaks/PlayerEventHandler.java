package sidben.villagertweaks;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;




public class PlayerEventHandler {


	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onEntityInteractEvent(EntityInteractEvent event)
	{
		/*
		 * OBS: This method is called whenever a player interacts with something (right-click)
		 */



       
	}

	
}
